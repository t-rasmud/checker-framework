package org.checkerframework.checker.determinism;

import com.sun.source.tree.*;
import com.sun.source.tree.Tree.Kind;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.compilermsgs.qual.CompilerMessageKey;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.determinism.qual.RequiresDetToString;
import org.checkerframework.checker.nullness.qual.Nullable;
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
                && !atypeFactory.isCollectionType(useType)) {
            checker.report(Result.failure(ORDERNONDET_ON_NONCOLLECTION), tree);
            return false;
        }

        // Raises an error if the annotation on the type argument of a collection (or iterator) is
        // a supertype of the annotation on the collection (or iterator).
        AnnotationMirror baseAnnotation = useType.getAnnotationInHierarchy(atypeFactory.NONDET);
        if (atypeFactory.isCollectionType(useType)) {
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
     * <p>Also, reports error in case of invalid field access on the lhs of an assignment. Accessing
     * {@code @Det} field of a {@code @NonDet} or {@code @PolyDet Object} on the lhs of an
     * assignment is illegal. Example:
     *
     * <pre><code>
     * &nbsp; class MyClass {
     * &nbsp; &nbsp; @Det df;
     * &nbsp; }
     *
     * &nbsp; @Det d1 = ...;
     * &nbsp; @Det d2 = ...;
     * &nbsp; Set s = new HashSet<>();
     * &nbsp; s.add(d1); s.add(d2);
     * &nbsp; @NonDet nd = s.iterator().next();
     * &nbsp; nd.df = 22;
     * </code></pre>
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
                // Assigning to a specific index of an "OrderNonDet" array is invalid.
                if (varTree.getKind() == Kind.ARRAY_ACCESS) {
                    if (AnnotationUtils.areSame(varAnno, atypeFactory.ORDERNONDET)
                            || AnnotationUtils.areSame(varAnno, atypeFactory.POLYDET)) {
                        checker.report(
                                Result.failure(INVALID_ARRAY_ASSIGNMENT, varAnno, exprAnno),
                                varTree);
                    }
                }
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
            iteratedType.replaceAnnotation(atypeFactory.POLYDET_UP);
        }
        if (valid) {
            this.commonAssignmentCheck(
                    var, iteratedType, node.getExpression(), "enhancedfor.type.incompatible");
        }

        return null;
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

    @Override
    public Void visitMethod(MethodTree node, Void p) {
        checkMethodSignatureForPolyQuals(node);
        checkRequiresDetToString(node);
        return super.visitMethod(node, p);
    }

    /**
     * Reports an error if polymorphic qualifier that does not affect instantiation is used in a
     * method signature without a polymorphic qualifier that does affect instantiation. Without at
     * least one qualifier that affects instantiation, the checker cannot resolve the other
     * polymorphic qualifiers.
     *
     * <p>Polymorphic qualifiers that do not affect instantiation: any polymorphic qualifier on the
     * return type and any of these annotations on a parameter or receiver type:
     * {@code @PolyDet("up")}, {@code @PolyDet("down")}, {@code @PolyDet("upDet")}, and
     * {@code @PolyDet("use")}.
     *
     * <p>Polymorphic qualifiers that do affect instantiation: {@code @PolyDet} and
     * {@code @PolyDet("noOrderNonDet")} on a parameter or receiver type.
     */
    private void checkMethodSignatureForPolyQuals(MethodTree methodTree) {
        // Errors that should be issued if @PolyDet or @PolyDet("noOrderNonDet") are not found.
        List<Pair<Result, Tree>> errors = new ArrayList<>();
        VariableTree receiver = methodTree.getReceiverParameter();
        // Don't check receiver annotation for static methods and constructors
        // as the receiver is null in these two cases.
        if (!ElementUtils.isStatic(TreeUtils.elementFromDeclaration(methodTree))
                && !TreeUtils.isConstructor(methodTree)) {
            AnnotatedDeclaredType receiverType =
                    atypeFactory.getAnnotatedType(methodTree).getReceiverType();
            AnnotationMirror anno = receiverType.getAnnotationInHierarchy(atypeFactory.NONDET);
            if (addPolyDetError(anno, receiver, errors)) {
                return; // found @PolyDet or @PolyDet("noOrderNonDet")
            }
        }

        for (VariableTree param : methodTree.getParameters()) {
            AnnotationMirror anno =
                    atypeFactory
                            .getAnnotatedType(param)
                            .getAnnotationInHierarchy(atypeFactory.NONDET);
            if (addPolyDetError(anno, param, errors)) {
                return; // found @PolyDet or @PolyDet("noOrderNonDet")
            }
        }

        if (methodTree.getReturnType() != null) {
            AnnotationMirror returnTypeAnno =
                    atypeFactory
                            .getAnnotatedType(methodTree.getReturnType())
                            .getAnnotationInHierarchy(atypeFactory.NONDET);
            addPolyDetError(returnTypeAnno, methodTree.getReturnType(), errors);
        }
        // This point is only reached if @PolyDet or @PolyDet("noOrderNonDet") were not found on
        // either the receiver or any parameter.
        for (Pair<Result, Tree> pair : errors) {
            checker.report(pair.first, pair.second);
        }
    }

    /**
     * Reports an error if {@code methodTree} represents a method annotated with
     * {@code @RequiresDetToString} but it overrides a method that is not annotated with
     * {@code @RequiresDetToString}.
     *
     * @param methodTree tree for the method to check
     */
    private void checkRequiresDetToString(MethodTree methodTree) {
        ExecutableElement methodElement = TreeUtils.elementFromDeclaration(methodTree);
        AnnotationMirror declAnnotation =
                atypeFactory.getDeclAnnotation(methodElement, RequiresDetToString.class);
        if (declAnnotation == null) {
            return;
        }

        Map<AnnotatedDeclaredType, ExecutableElement> overridden =
                AnnotatedTypes.overriddenMethods(elements, atypeFactory, methodElement);
        for (Map.Entry<AnnotatedDeclaredType, ExecutableElement> entry : overridden.entrySet()) {
            AnnotationMirror overiddenAnnotation =
                    atypeFactory.getDeclAnnotation(entry.getValue(), RequiresDetToString.class);
            if (overiddenAnnotation == null) {
                checker.report(
                        Result.failure(
                                "invalid.requiresdettostring",
                                ElementUtils.enclosingClass(entry.getValue()).asType()),
                        methodTree);
            }
        }
    }

    /**
     * Adds a pair of an Result and {@code tree} to {@code errors} if {@code anno} is
     * {@code @PolyDet("up")}, {@code @PolyDet("down")}, {@code @PolyDet("upDet")}, or
     * {@code @PolyDet("use")}. Returns true if {@code anno} is {@code @PolyDet} or
     * {@code @PolyDet("noOrderNonDet")}; otherwise false.
     *
     * @param anno a possibly null annotation to check
     * @param tree place to report error
     * @param errors list to add pair of Result and {@code tree}
     * @return true if {@code anno} is {@code @PolyDet} or{@code @PolyDet("noOrderNonDet")};
     *     otherwise false
     */
    private boolean addPolyDetError(
            @Nullable AnnotationMirror anno, Tree tree, List<Pair<Result, Tree>> errors) {
        if (anno == null) {
            return false;
        }
        if (AnnotationUtils.areSameByClass(anno, PolyDet.class)) {
            String elemValue = AnnotationUtils.getElementValue(anno, "value", String.class, true);
            if (elemValue.equals("") || elemValue.equals("noOrderNonDet")) {
                // found @PolyDet or @PolyDet("noOrderNonDet"); no error
                return true;
            } else {
                @SuppressWarnings("compilermessages")
                @CompilerMessageKey String errorKey = "invalid.polydet." + elemValue.toLowerCase();
                errors.add(Pair.of(Result.failure(errorKey), tree));
            }
        }
        return false;
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

        int lastParamIndex = params.size() - 1;
        for (int index = 0; index < args.size(); index++) {
            ExpressionTree arg = args.get(index);

            // If the last parameter is a VarArg, then the number of arguments
            // could be greater than the number of parameters.
            // In this case, check all the arguments at indices greater than
            // size of the paremeter list against the last parameter.
            VariableElement param = params.get(Math.min(lastParamIndex, index));

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
     * Reports the given {@code errorMessage} if {@code elementAnno} is not a valid element type of
     * a collection or array with {@code collectionAnno}.
     *
     * @param elementAnno the annotation of the element type of an array or collection
     * @param collectionAnno the annotation of an array or collection
     * @param tree the tree to report errors on
     * @param errorMessage the error message to report
     * @return true if {@code elementAnno} is a subtype of {@code collectionAnno} and it's not the
     *     case that {@code collectionAnno} is {@code @NonDet} and {@code elementAnno} is
     *     {@code @Det}, {@code @OrderNonDet}, or {@code @PolyDet}, false otherwise
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
                        || AnnotationUtils.areSame(elementAnno, atypeFactory.ORDERNONDET)
                        || AnnotationUtils.areSameByName(elementAnno, atypeFactory.POLYDET))) {
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
        if (atypeFactory.isTreeSet(constructorResultType)
                || atypeFactory.isTreeMap(constructorResultType)) {
            AnnotationMirror explicitAnno = atypeFactory.getNewClassAnnotation(newClassTree);
            if (explicitAnno != null
                    && AnnotationUtils.areSameByClass(explicitAnno, OrderNonDet.class)) {
                AnnotationMirror constructorResult =
                        constructorResultType.getAnnotationInHierarchy(atypeFactory.NONDET);
                if (AnnotationUtils.areSameByClass(constructorResult, PolyDet.class)) {
                    checker.report(
                            Result.failure(
                                    DeterminismVisitor.INVALID_COLLECTION_CONSTRUCTOR_INVOCATION,
                                    constructorResultType),
                            newClassTree);
                }
            }
        }
        super.checkConstructorInvocation(invocation, constructor, newClassTree);
    }

    @Override
    protected void checkConstructorResult(
            AnnotatedTypeMirror.AnnotatedExecutableType constructorType,
            ExecutableElement constructorElement) {}
}
