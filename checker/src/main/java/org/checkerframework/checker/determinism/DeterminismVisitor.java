package org.checkerframework.checker.determinism;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.checker.determinism.qual.RequiresDetToString;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeValidator;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.common.basetype.TypeValidator;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedPrimitiveType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedTypeVariable;
import org.checkerframework.framework.util.AnnotatedTypes;
import org.checkerframework.javacutil.*;

/** Visitor for the determinism type-system. */
public class DeterminismVisitor extends BaseTypeVisitor<DeterminismAnnotatedTypeFactory> {
    /** Calls the superclass constructor. */
    public DeterminismVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    /** Error message key for use of {@code @OrderNonDet} on non-collections and non-arrays. */
    private static final @CompilerMessageKey String ORDERNONDET_ON_NONCOLLECTION =
            "ordernondet.on.noncollection.and.nonarray";
    /**
     * Error message key for collections whose type is a subtype of their element types, or whose
     * type is {@code @NonDet} with element type {@code @Det} or {@code @OrderNonDet}.
     */
    private static final @CompilerMessageKey String INVALID_ELEMENT_TYPE = "invalid.element.type";
    /**
     * Error message key for arrays whose type is a subtype of their component types, or whose type
     * is {@code @NonDet} with element type {@code @Det} or {@code @OrderNonDet}.
     */
    private static final @CompilerMessageKey String INVALID_ARRAY_COMPONENT_TYPE =
            "invalid.array.component.type";
    /** Error message key for assignment to a deterministic array at a non-deterministic index. */
    public static final @CompilerMessageKey String INVALID_ARRAY_ASSIGNMENT =
            "invalid.array.assignment";

    /**
     * Error message key for assignment to a deterministic field via a non-deterministic expression.
     */
    public static final @CompilerMessageKey String INVALID_FIELD_ASSIGNMENT =
            "invalid.field.assignment";
    /**
     * Error message key for collections whose type is a subtype of the upper bound of their type
     * arguments, or whose type is {@code @NonDet} with element type {@code @Det} or
     * {@code @OrderNonDet}.
     */
    public static final @CompilerMessageKey String INVALID_UPPER_BOUND_TYPE_ARGUMENT =
            "invalid.upper.bound.on.type.argument";
    /**
     * Error message key for arrays whose type is a subtype of the upper bound of their type
     * arguments, or whose type is {@code @NonDet} with element type {@code @Det} or
     * {@code @OrderNonDet}.
     */
    public static final @CompilerMessageKey String INVALID_UPPER_BOUND_TYPE_ARGUMENT_ARRAY =
            "invalid.upper.bound.on.type.argument.of.array";
    /** Error message for expressions of the form "new @Det HashSet" */
    public static final @CompilerMessageKey String INVALID_COLLECTION_CONSTRUCTOR_INVOCATION =
            "invalid.collection.constructor.invocation";

    private final ExecutableElement stringToString =
            TreeUtils.getMethod("java.lang.Object", "toString", 0, atypeFactory.getProcessingEnv());
    /**
     * The lower bound for exception parameters is {@code @Det}.
     *
     * @return set of lower bound annotations for exception parameters
     */
    @Override
    protected Set<? extends AnnotationMirror> getExceptionParameterLowerBoundAnnotations() {
        return Collections.singleton(atypeFactory.DET);
    }

    /**
     * Reports errors for the following conditions:
     *
     * <ol>
     *   <li>When a non-collection is annotated as {@code @OrderNonDet}.
     *   <li>When the annotation on the type argument of a Collection or Iterator is a supertype of
     *       the annotation on the Collection. Example: {@code @Det List<@OrderNonDet String>}.
     *   <li>When the annotation on the upper bound of the type argument of a Collection or Iterator
     *       is a supertype of the annotation on the Collection. Example: {@code @Det List<T
     *       extends @NonDet Object>}.
     * </ol>
     *
     * @param declarationType the type of any non-primitive, non-array class (TypeElement)
     * @param useType the use of the {@code declarationType} class (instance type)
     * @param tree the tree where the type is used
     * @return true if the annotation is valid and false otherwise (in which case an error is also
     *     reported)
     */
    @Override
    public boolean isValidUse(
            AnnotatedDeclaredType declarationType, AnnotatedDeclaredType useType, Tree tree) {
        if (TreeUtils.isClassLiteral(tree)) {
            // Don't validate class literals
            return true;
        }
        // Raises an error if a non-collection type is annotated with @OrderNonDet.
        if (useType.hasAnnotation(atypeFactory.ORDERNONDET)
                && !atypeFactory.mayBeOrderNonDet(useType)) {
            checker.report(Result.failure(ORDERNONDET_ON_NONCOLLECTION), tree);
            return false;
        }

        // Raises an error if the annotation on the type argument of a collection (or iterator) is
        // a supertype of the annotation on the collection (or iterator).
        AnnotationMirror baseAnnotation = useType.getAnnotationInHierarchy(atypeFactory.NONDET);
        if (atypeFactory.mayBeOrderNonDet(useType)) {
            for (AnnotatedTypeMirror argType : useType.getTypeArguments()) {
                if (!argType.getAnnotations().isEmpty()) {
                    AnnotationMirror argAnnotation =
                            argType.getAnnotationInHierarchy(atypeFactory.NONDET);
                    if (!isValidElementType(
                            argAnnotation, baseAnnotation, tree, INVALID_ELEMENT_TYPE)) {
                        return false;
                    }
                }
                if (argType.getKind() == TypeKind.TYPEVAR) {
                    AnnotatedTypeMirror argTypeUpperBound =
                            ((AnnotatedTypeVariable) argType).getUpperBound();
                    AnnotationMirror typevarAnnotation =
                            getUpperBound(atypeFactory, argTypeUpperBound);
                    if (!isSubtypeError(
                            typevarAnnotation,
                            baseAnnotation,
                            tree,
                            INVALID_UPPER_BOUND_TYPE_ARGUMENT)) {
                        return false;
                    }
                }
                if (argType.getKind() == TypeKind.WILDCARD) {
                    AnnotatedTypeMirror argTypeExtendsBound =
                            ((AnnotatedTypeMirror.AnnotatedWildcardType) argType).getExtendsBound();
                    AnnotationMirror typevarAnnotation =
                            getUpperBound(atypeFactory, argTypeExtendsBound);
                    if (!isSubtypeError(
                            typevarAnnotation,
                            baseAnnotation,
                            tree,
                            INVALID_UPPER_BOUND_TYPE_ARGUMENT)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Reports an error if {@code @OrderNonDet} is used with a primitive type.
     *
     * @param type the use of the primitive type
     * @param tree the tree where the type is used; used only for error reporting
     * @return true if the annotation is valid and false otherwise
     */
    @Override
    public boolean isValidUse(AnnotatedPrimitiveType type, Tree tree) {
        if (type.hasAnnotation(atypeFactory.ORDERNONDET)) {
            checker.report(Result.failure(ORDERNONDET_ON_NONCOLLECTION), tree);
            return false;
        }
        return true;
    }

    /**
     * Reports errors for the following conditions:
     *
     * <ol>
     *   <li>If the component type of an array has an annotation that is a supertype of the array
     *       annotation. Example: {@code @NonDet int @Det[]} is invalid.
     *   <li>If the component type is a type variable and if the annotation on the upper bound of
     *       the type variable is a supertype of the array annotation. Example: {@code <T
     *       extends @NonDet Object> T @Det[]} is invalid.
     * </ol>
     *
     * @param type the array type use
     * @param tree the tree where the type is used
     * @return true if the annotation is valid and false otherwise
     */
    @Override
    public boolean isValidUse(AnnotatedArrayType type, Tree tree) {
        AnnotatedTypeMirror componentType = type.getComponentType();
        if (!type.getAnnotations().isEmpty()) {
            AnnotationMirror arrayType = type.getAnnotationInHierarchy(atypeFactory.NONDET);
            if (!componentType.getAnnotations().isEmpty()) {
                AnnotationMirror componentAnno =
                        componentType.getAnnotationInHierarchy(atypeFactory.NONDET);
                if (!isValidElementType(
                        componentAnno, arrayType, tree, INVALID_ARRAY_COMPONENT_TYPE)) {
                    return false;
                }
                if (componentType.getKind() == TypeKind.TYPEVAR) {
                    AnnotationMirror componentUpperBoundAnnotation =
                            ((AnnotatedTypeVariable) componentType)
                                    .getUpperBound()
                                    .getAnnotationInHierarchy(atypeFactory.NONDET);
                    if (!isValidElementType(
                            componentUpperBoundAnnotation,
                            arrayType,
                            tree,
                            INVALID_UPPER_BOUND_TYPE_ARGUMENT_ARRAY)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public Void visitInstanceOf(InstanceOfTree node, Void p) {
        // skip super call.  There's no need to validate the type in the instanceOf.
        return null;
    }

    /**
     * Returns annotation on the upper bound of {@code argTypeUpperBound}
     *
     * <p>Example 1: If this method is called with {@code argTypeUpperBound} as {@code Z
     * extends @NonDet T}, it returns {@code NonDet}.
     *
     * <p>Example 2: If this method is called with {@code argTypeUpperBound} as {@code @Det Z}, it
     * returns {@code Det}.
     */
    public static AnnotationMirror getUpperBound(
            DeterminismAnnotatedTypeFactory factory, AnnotatedTypeMirror argTypeUpperBound) {
        AnnotationMirror typevarAnnotation =
                argTypeUpperBound.getAnnotationInHierarchy(factory.NONDET);
        // typevarAnnotation is null for "<Z>  List<? extends Z>", "<Z,T>  List<T extends Z>"
        while (typevarAnnotation == null) {
            argTypeUpperBound =
                    ((AnnotatedTypeMirror.AnnotatedTypeVariable) argTypeUpperBound).getUpperBound();
            typevarAnnotation = argTypeUpperBound.getAnnotationInHierarchy(factory.NONDET);
        }
        return typevarAnnotation;
    }

    /**
     * Reports an error in case of invalid access of an array element on the lhs of an assignment.
     * This is to prevent side-effects to arrays. Example:
     *
     * <pre><code>
     * &nbsp; @Det int @Det [] x;
     * &nbsp; @NonDet int i;
     * &nbsp; x[i] = y;
     * </code></pre>
     *
     * The array access {@code x[i]} is flagged as an error.
     *
     * <p>Also, reports error in case of invalid field access on the lhs of an assignment.
     *
     * @param varTree the AST node for the lvalue
     * @param valueExp the AST node for the rvalue (the new value)
     * @param errorKey the error message to use if the check fails (must be a compiler message key)
     */
    @Override
    protected void commonAssignmentCheck(
            Tree varTree, ExpressionTree valueExp, @CompilerMessageKey String errorKey) {
        AnnotatedTypeMirror expressionType;
        switch (varTree.getKind()) {
            case ARRAY_ACCESS:
                Tree indexTree = ((ArrayAccessTree) varTree).getIndex();
                expressionType = atypeFactory.getAnnotatedType(indexTree);
                break;
            case MEMBER_SELECT:
            case IDENTIFIER:
                Element field = TreeUtils.elementFromUse((ExpressionTree) varTree);
                if (field.getKind() == ElementKind.FIELD && !ElementUtils.isStatic(field)) {
                    expressionType = atypeFactory.getReceiverType((ExpressionTree) varTree);
                } else {
                    expressionType = null;
                }
                break;
            case VARIABLE:
                Element fieldDecl = TreeUtils.elementFromDeclaration((VariableTree) varTree);
                if (fieldDecl.getKind() == ElementKind.FIELD && !ElementUtils.isStatic(fieldDecl)) {
                    expressionType = atypeFactory.getSelfType(varTree);
                } else {
                    expressionType = null;
                }
                break;
            default:
                expressionType = null;
        }
        if (expressionType != null) {
            AnnotationMirror NONDET = atypeFactory.NONDET;
            AnnotationMirror exprAnno = expressionType.getEffectiveAnnotationInHierarchy(NONDET);
            AnnotatedTypeMirror varType = atypeFactory.getAnnotatedTypeLhs(varTree);
            AnnotationMirror varAnno = varType.getEffectiveAnnotationInHierarchy(NONDET);
            if (atypeFactory.getQualifierHierarchy().isSubtype(exprAnno, varAnno)) {
                super.commonAssignmentCheck(varTree, valueExp, errorKey);
            } else if (varTree.getKind() == Kind.ARRAY_ACCESS) {
                checker.report(
                        Result.failure(INVALID_ARRAY_ASSIGNMENT, varAnno, exprAnno), varTree);
            } else {
                checker.report(
                        Result.failure(INVALID_FIELD_ASSIGNMENT, varAnno, exprAnno), varTree);
            }
        } else {
            super.commonAssignmentCheck(varTree, valueExp, errorKey);
        }
    }

    /**
     * When an array of type {@code @OrderNonDet} or {@code @NonDet} is accessed, this method
     * annotates the type of the array access expression (equivalently, the array element) as
     * {@code @NonDet}. Example:
     *
     * <pre><code>
     * &nbsp; @Det int @NonDet int[] arr;
     * &nbsp; int val = arr[0];
     * </code></pre>
     *
     * In the code above, type of val gets annotated as @NonDet.
     *
     * <p>Note: If we were to replace every array access with this rule, the checker would allow
     * invalid assignments to array elements. Example:
     *
     * <pre><code>
     * &nbsp; @Det int @OrderNonDet [] x;
     * &nbsp; @NonDet int i;
     * &nbsp; x[i] = y;
     * </code></pre>
     *
     * Here, we expect the checker to flag the assignment {@code x[i] = y} as an error. Had we
     * replaced the type of every {@code @OrderNonDet} and {@code @NonDet} array access with
     * {@code @NonDet}, the array access {@code x[i]} would have the type {@code @NonDet} and this
     * assignment would not be flagged as an error.
     *
     * <p>NOTE: We override {@code commonAssignmentCheck} and not {@code visitArrayAccess} because
     * the checker framework treats x[i] as an lvalue like array access. It is possible to
     * distinguish whether a "[]" operator is in an lvalue or an rvalue position. But, the {@code
     * visitArrayAccess} method does not give access to valueType (the annotated type of rhs value)
     * like in {@code commonAssignmentCheck} below, making it difficult to replace the annotation on
     * the rvalue.
     *
     * @param varType the annotated type of the variable
     * @param valueType the annotated type of the value
     * @param valueTree the location to use when reporting the error message
     * @param errorKey the error message to use if the check fails (must be a compiler message key)
     */
    @Override
    protected void commonAssignmentCheck(
            AnnotatedTypeMirror varType,
            AnnotatedTypeMirror valueType,
            Tree valueTree,
            @CompilerMessageKey String errorKey) {
        if (valueTree.getKind() == Tree.Kind.ARRAY_ACCESS) {
            ArrayAccessTree arrTree = (ArrayAccessTree) valueTree;
            AnnotatedArrayType arrType =
                    (AnnotatedArrayType) atypeFactory.getAnnotatedType(arrTree.getExpression());
            AnnotationMirror arrTopType = arrType.getAnnotationInHierarchy(atypeFactory.NONDET);
            if (AnnotationUtils.areSame(arrTopType, atypeFactory.ORDERNONDET)
                    || AnnotationUtils.areSame(arrTopType, atypeFactory.NONDET)) {
                valueType.replaceAnnotation(atypeFactory.NONDET);
            }
            if (AnnotationUtils.areSame(arrTopType, atypeFactory.POLYDET)) {
                valueType.replaceAnnotation(atypeFactory.POLYDET_UP);
            }
        }
        super.commonAssignmentCheck(varType, valueType, valueTree, errorKey);
    }

    // Hack: Remove this after it's fixed on the master branch.
    @Override
    public Void visitEnhancedForLoop(EnhancedForLoopTree node, Void p) {
        AnnotatedTypeMirror var = this.atypeFactory.getAnnotatedTypeLhs(node.getVariable());
        AnnotatedTypeMirror iterableType = this.atypeFactory.getAnnotatedType(node.getExpression());
        AnnotatedTypeMirror iteratedType =
                AnnotatedTypes.getIteratedType(
                        this.checker.getProcessingEnvironment(), this.atypeFactory, iterableType);
        boolean valid = this.validateTypeOf(node.getVariable());
        if (iterableType.hasAnnotation(atypeFactory.ORDERNONDET)
                || iterableType.hasAnnotation(atypeFactory.NONDET)) {
            iteratedType.replaceAnnotation(atypeFactory.NONDET);
        }
        if (iterableType.hasAnnotation(atypeFactory.POLYDET)) {
            iteratedType.replaceAnnotation(atypeFactory.POLYDET);
        }
        if (valid) {
            this.commonAssignmentCheck(
                    var, iteratedType, node.getExpression(), "enhancedfor.type.incompatible");
        }

        return super.visitEnhancedForLoop(node, p);
    }

    /**
     * Reports an error if the condition of the ternary expression {@code node} is not {@code @Det}.
     */
    @Override
    public Void visitConditionalExpression(ConditionalExpressionTree node, Void p) {
        Void result = super.visitConditionalExpression(node, p);
        ExpressionTree conditionalExpression = node.getCondition();
        checkForDetConditional(conditionalExpression);
        return result;
    }

    /** Reports an error if the condition of the If statement {@code node} is not {@code @Det}. */
    @Override
    public Void visitIf(IfTree node, Void aVoid) {
        Void result = super.visitIf(node, aVoid);
        ExpressionTree conditionalExpression = node.getCondition();
        checkForDetConditional(conditionalExpression);
        return result;
    }

    /** Reports an error if the condition of the For loop {@code node} is not {@code @Det}. */
    @Override
    public Void visitForLoop(ForLoopTree node, Void aVoid) {
        Void result = super.visitForLoop(node, aVoid);
        ExpressionTree conditionalExpression = node.getCondition();
        checkForDetConditional(conditionalExpression);
        return result;
    }

    /** Reports an error if the condition of the While loop {@code node} is not {@code @Det}. */
    @Override
    public Void visitWhileLoop(WhileLoopTree node, Void aVoid) {
        Void result = super.visitWhileLoop(node, aVoid);
        ExpressionTree conditionalExpression = node.getCondition();
        checkForDetConditional(conditionalExpression);
        return result;
    }

    /** Reports an error if the condition of the Do While loop {@code node} is not {@code @Det}. */
    @Override
    public Void visitDoWhileLoop(DoWhileLoopTree node, Void aVoid) {
        Void result = super.visitDoWhileLoop(node, aVoid);
        ExpressionTree conditionalExpression = node.getCondition();
        checkForDetConditional(conditionalExpression);
        return result;
    }

    /** if {@code conditionalExpression} does not have the type {@code @Det}, reports an error. */
    private void checkForDetConditional(ExpressionTree conditionalExpression) {
        // TODO-rashmi: conditionalExpression is null for some condition in buildJdk
        if (conditionalExpression == null) {
            return;
        }
        AnnotatedTypeMirror conditionType = atypeFactory.getAnnotatedType(conditionalExpression);
        if (!conditionType.hasAnnotation(atypeFactory.DET)
                && checker.getLintOption("enableconditionaltypecheck", false)) {
            checker.report(
                    Result.failure(
                            "invalid.type.on.conditional",
                            conditionType.getAnnotationInHierarchy(atypeFactory.NONDET)),
                    conditionalExpression);
        }
    }

    /**
     * Reports an error if {@code @PolyDet("up")}, {@code @PolyDet("down")},
     * {@code @PolyDet("upDet")}, or {@code @PolyDet("use")} is written on a formal parameter or a
     * return type and none of the formal parameters or the receiver has the type {@code @PolyDet}
     * or {@code @PolyDet("noOrderNonDet")}. These variants of {@code @PolyDet} should only be
     * written on use sites (not instantiation sites). Without a corresponding {@code @PolyDet} or
     * {@code @PolyDet("noOrderNonDet")} on at least on of the formal parameters,
     * {@code @PolyDet("up")}, {@code @PolyDet("down")}, {@code @PolyDet("upDet")}, or
     * {@code @PolyDet("use")} cannot be instantiated correctly.
     *
     * <p>NOTE: {@code @PolyDet("noOrderNonDet")} is a polymorphic type that can resolve to
     * {@code @NonDet} or {@code @Det}, but nor {@code @OrderNonDet}.
     */
    @Override
    public Void visitMethod(MethodTree node, Void p) {
        HashSet<AnnotationMirror> paramAnnotations = new HashSet<>();
        VariableTree receiverParam = node.getReceiverParameter();
        if (receiverParam != null) {
            paramAnnotations.add(
                    atypeFactory
                            .getAnnotatedType(receiverParam)
                            .getAnnotationInHierarchy(atypeFactory.NONDET));
        } else if (!node.getModifiers().getFlags().contains(Modifier.STATIC)) {
            paramAnnotations.add(atypeFactory.POLYDET);
        }
        for (VariableTree param : node.getParameters()) {
            paramAnnotations.add(
                    atypeFactory
                            .getAnnotatedType(param)
                            .getAnnotationInHierarchy(atypeFactory.NONDET));
        }
        boolean isPolyPresent = false;
        boolean isPolyUpPresent = false;
        boolean isPolyDownPresent = false;
        boolean isPolyUsePresent = false;
        boolean isPolyUpDetPresent = false;
        for (AnnotationMirror atm : paramAnnotations) {
            if (AnnotationUtils.areSame(atm, atypeFactory.POLYDET_UP)) {
                isPolyUpPresent = true;
            }
            if (AnnotationUtils.areSame(atm, atypeFactory.POLYDET_DOWN)) {
                isPolyDownPresent = true;
            }
            if (AnnotationUtils.areSame(atm, atypeFactory.POLYDET_UPDET)) {
                isPolyUpDetPresent = true;
            }
            if (AnnotationUtils.areSame(atm, atypeFactory.POLYDET_USE)) {
                isPolyUsePresent = true;
            }
            if (AnnotationUtils.areSame(atm, atypeFactory.POLYDET)
                    || AnnotationUtils.areSame(atm, atypeFactory.POLYDET_NOORDERNONDET)) {
                isPolyPresent = true;
            }
        }
        if (!isPolyPresent) {
            AnnotationMirror returnTypeAnno =
                    atypeFactory
                            .getAnnotatedType(node.getReturnType())
                            .getAnnotationInHierarchy(atypeFactory.NONDET);
            if (isPolyUpPresent
                    || AnnotationUtils.areSame(returnTypeAnno, atypeFactory.POLYDET_UP)) {
                checker.report(Result.failure("invalid.polydet.up"), node);
            }
            if (isPolyDownPresent
                    || AnnotationUtils.areSame(returnTypeAnno, atypeFactory.POLYDET_DOWN)) {
                checker.report(Result.failure("invalid.polydet.down"), node);
            }
            if (isPolyUpDetPresent
                    || AnnotationUtils.areSame(returnTypeAnno, atypeFactory.POLYDET_UPDET)) {
                checker.report(Result.failure("invalid.polydet.updet"), node);
            }
            if (isPolyUsePresent
                    || AnnotationUtils.areSame(returnTypeAnno, atypeFactory.POLYDET_USE)) {
                checker.report(Result.failure("invalid.polydet.use"), node);
            }
        }
        return super.visitMethod(node, p);
    }

    /**
     * If the declaration of {@code node} is annotated with {@code @RequiresDetToString}, checks
     * that the declared type of every {@code Det} argument of {@code node} corresponding to a
     * parameter of type {@code Object} (or {@code Object[]}) overrides {@code toString} returning a
     * {@code @Det String} or {@code @PolyDet}. Otherwise issues an error.
     */
    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        ExecutableElement methodElement = TreeUtils.elementFromUse(node);
        AnnotationMirror declAnnotation =
                atypeFactory.getDeclAnnotation(methodElement, RequiresDetToString.class);
        if (declAnnotation == null) {
            return super.visitMethodInvocation(node, p);
        }

        List<? extends VariableElement> params = methodElement.getParameters();
        List<? extends ExpressionTree> args = node.getArguments();

        for (int index = 0; index < args.size(); index++) {
            ExpressionTree arg = args.get(index);
            VariableElement param = params.get(index);

            boolean isParamObjectArray = false;
            TypeMirror paramType = param.asType();
            if (paramType.getKind() == TypeKind.ARRAY) {
                TypeMirror compType = ((ArrayType) paramType).getComponentType();
                isParamObjectArray = TypesUtils.isObject(compType);
            }
            if (!TypesUtils.isObject(paramType) && !isParamObjectArray) {
                continue;
            }

            AnnotatedTypeMirror argType = atypeFactory.getAnnotatedType(arg);
            if (argType.hasAnnotation(atypeFactory.DET)) {
                Pair<AnnotatedDeclaredType, ExecutableElement> overriddenMethod =
                        AnnotatedTypes.getOverriddenMethod(
                                argType, stringToString, atypeFactory.getProcessingEnv());
                if (!atypeFactory
                                .getAnnotatedType(overriddenMethod.second)
                                .getReturnType()
                                .hasAnnotation(atypeFactory.DET)
                        && !atypeFactory
                                .getAnnotatedType(overriddenMethod.second)
                                .getReturnType()
                                .hasAnnotation(atypeFactory.POLYDET)) {
                    checker.report(
                            Result.failure(
                                    "nondeterministic.tostring", argType.getUnderlyingType()),
                            node);
                    break;
                }
            }
        }
        return super.visitMethodInvocation(node, p);
    }

    /**
     * Reports the given {@code errorMessage} if {@code subAnnotation} is not a subtype of {@code
     * superAnnotation}.
     *
     * @return true if {@code subAnnotation} is a subtype of {@code superAnnotation}, false
     *     otherwise
     */
    private boolean isSubtypeError(
            AnnotationMirror subAnnotation,
            AnnotationMirror superAnnotation,
            Tree tree,
            @CompilerMessageKey String errorMessage) {
        if (atypeFactory.getQualifierHierarchy().isSubtype(subAnnotation, superAnnotation)) {
            return true;
        }
        checker.report(Result.failure(errorMessage, subAnnotation, superAnnotation), tree);
        return false;
    }

    /**
     * Reports the given {@code errorMessage} if {@code subAnnotation} is not a valid element type
     * of a collection or array with {@code collectionAnno}.
     *
     * @param elementAnno the annotation of the element type of an array or collection
     * @param collectionAnno the annotation of an array or collection
     * @param tree the tree to report errors on
     * @param errorMessage the error message to report
     * @return true if {@code elementAnno} is a subtype of {@code supperAnnotation} and it's not the
     *     case that {@code collectionAnno} is {@code @NonDet} and {@code elementAnno} is
     *     {@code @Det} or {@code @OrderNonDet}, false otherwise
     */
    private boolean isValidElementType(
            AnnotationMirror elementAnno,
            AnnotationMirror collectionAnno,
            Tree tree,
            @CompilerMessageKey String errorMessage) {
        if (!atypeFactory.getQualifierHierarchy().isSubtype(elementAnno, collectionAnno)) {
            checker.report(Result.failure(errorMessage, elementAnno, collectionAnno), tree);
            return false;
        }
        if (AnnotationUtils.areSame(collectionAnno, atypeFactory.NONDET)
                && (AnnotationUtils.areSame(elementAnno, atypeFactory.DET)
                        || AnnotationUtils.areSame(elementAnno, atypeFactory.ORDERNONDET))) {
            checker.report(Result.failure(errorMessage, elementAnno, collectionAnno), tree);
            return false;
        }
        return true;
    }

    @Override
    protected TypeValidator createTypeValidator() {
        return new BaseTypeValidator(checker, this, atypeFactory) {
            @Override
            protected boolean shouldCheckTopLevelDeclaredType(AnnotatedTypeMirror type, Tree tree) {
                // Always check.
                return true;
            }

            @Override
            protected void reportInvalidAnnotationsOnUse(AnnotatedTypeMirror type, Tree p) {}
        };
    }

    /**
     * Reports an error if {@code newClassTree} represents explicitly constructing a
     *
     * <ol>
     *   <li>{@code @OrderNonDet TreeSet}
     *   <li>{@code @OrderNonDet TreeMap}
     * </ol>
     *
     * <p>Also reports an error if the result of the constructor would resolve to any variant of
     * {@code @PolyDet}.
     *
     * @param invocation annotated declared type of constructor invocation
     * @param constructor annotated executable type of the constructor
     * @param newClassTree a tree representing instantiating a class
     */
    @Override
    protected void checkConstructorInvocation(
            AnnotatedDeclaredType invocation,
            AnnotatedTypeMirror.AnnotatedExecutableType constructor,
            NewClassTree newClassTree) {
        AnnotatedTypeMirror constructorResultType = constructor.getReturnType();
        AnnotationMirror explicitAnno = atypeFactory.getNewClassAnnotation(newClassTree);
        if (atypeFactory.isTreeSet(constructorResultType)
                || atypeFactory.isTreeMap(constructorResultType)) {
            if (AnnotationUtils.areSame(explicitAnno, atypeFactory.ORDERNONDET)
                    || AnnotationUtils.areSameByName(
                            constructorResultType.getAnnotationInHierarchy(atypeFactory.NONDET),
                            atypeFactory.POLYDET)) {
                checker.report(
                        Result.failure(
                                DeterminismVisitor.INVALID_COLLECTION_CONSTRUCTOR_INVOCATION,
                                constructorResultType),
                        newClassTree);
            }
        }
        super.checkConstructorInvocation(invocation, constructor, newClassTree);
    }

    @Override
    protected void checkConstructorResult(
            AnnotatedTypeMirror.AnnotatedExecutableType constructorType,
            ExecutableElement constructorElement) {}
}
