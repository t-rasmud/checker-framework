package org.checkerframework.checker.determinism;

import com.sun.source.tree.*;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.qual.PolyAll;
import org.checkerframework.framework.source.Result;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.poly.QualifierPolymorphism;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.framework.util.GraphQualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.javacutil.*;

/** The annotated type factory for the determinism type-system. */
public class DeterminismAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    /** The @NonDet annotation. */
    public final AnnotationMirror NONDET = AnnotationBuilder.fromClass(elements, NonDet.class);
    /** The @OrderNonDet annotation. */
    public final AnnotationMirror ORDERNONDET =
            AnnotationBuilder.fromClass(elements, OrderNonDet.class);
    /** The @Det annotation. */
    public final AnnotationMirror DET = AnnotationBuilder.fromClass(elements, Det.class);
    /** The @PolyDet annotation. */
    public final AnnotationMirror POLYDET;
    /** The @PolyDet("up") annotation. */
    public final AnnotationMirror POLYDET_UP;
    /** The @PolyDet("down") annotation. */
    public final AnnotationMirror POLYDET_DOWN;
    /** The @PolyDet("use") annotation. */
    public final AnnotationMirror POLYDET_USE;

    /** The java.util.Set interface. */
    private final TypeMirror setInterfaceTypeMirror =
            TypesUtils.typeFromClass(Set.class, types, processingEnv.getElementUtils());
    /** The java.util.List interface. */
    private final TypeMirror listInterfaceTypeMirror =
            TypesUtils.typeFromClass(List.class, types, processingEnv.getElementUtils());
    /** The java.util.Collection class. */
    private final TypeMirror collectionInterfaceTypeMirror =
            TypesUtils.typeFromClass(Collection.class, types, processingEnv.getElementUtils());
    /** The java.util.Map class. */
    private final TypeMirror mapInterfaceTypeMirror =
            TypesUtils.typeFromClass(Map.class, types, processingEnv.getElementUtils());
    /** The java.util.Iterator class. */
    private final TypeMirror iteratorTypeMirror =
            TypesUtils.typeFromClass(Iterator.class, types, processingEnv.getElementUtils());
    /** The java.util.Arrays class. */
    private final TypeMirror arraysTypeMirror =
            TypesUtils.typeFromClass(Arrays.class, types, processingEnv.getElementUtils());
    /** The java.util.Collections class. */
    private final TypeMirror collectionsTypeMirror =
            TypesUtils.typeFromClass(Collections.class, types, processingEnv.getElementUtils());
    /** The java.util.HashSet class. */
    private final TypeMirror hashSetTypeMirror =
            TypesUtils.typeFromClass(HashSet.class, types, processingEnv.getElementUtils());
    /** The java.util.LinkedHashSet class. */
    private final TypeMirror linkedHashSetTypeMirror =
            TypesUtils.typeFromClass(LinkedHashSet.class, types, processingEnv.getElementUtils());
    /** The java.util.HashSet class. */
    private final TypeMirror treeSetTypeMirror =
            TypesUtils.typeFromClass(TreeSet.class, types, processingEnv.getElementUtils());
    /** The java.util.HashMap class. */
    private final TypeMirror hashMapTypeMirror =
            TypesUtils.typeFromClass(HashMap.class, types, processingEnv.getElementUtils());
    /** The java.util.LinkedHashMap class. */
    private final TypeMirror linkedHashMapTypeMirror =
            TypesUtils.typeFromClass(LinkedHashMap.class, types, processingEnv.getElementUtils());
    /** The java.util.TreeMap class. */
    private final TypeMirror treeMapTypeMirror =
            TypesUtils.typeFromClass(TreeMap.class, types, processingEnv.getElementUtils());

    /** Comma separated list of deterministic system properties */
    private final List<String> inputProperties;

    /** The Map.get method. */
    private final ExecutableElement mapGet;

    /** The Map.getOrDefault method. */
    private final ExecutableElement mapGetOrDefault;

    /** The Object.equals method. */
    private final ExecutableElement equals;

    /** Creates {@code @PolyDet} annotation mirror constants. */
    public DeterminismAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);

        POLYDET = newPolyDet("");
        POLYDET_UP = newPolyDet("up");
        POLYDET_DOWN = newPolyDet("down");
        POLYDET_USE = newPolyDet("use");

        this.inputProperties = Collections.unmodifiableList(buildInputProperties());

        mapGet = TreeUtils.getMethod("java.util.Map", "get", 1, processingEnv);
        mapGetOrDefault = TreeUtils.getMethod("java.util.Map", "getOrDefault", 2, processingEnv);
        equals = TreeUtils.getMethod("java.lang.Object", "equals", 1, processingEnv);

        postInit();
    }

    /**
     * Returns a list of properties supplied by the user via the command line option
     * "-AinputProperties".
     */
    private List<String> buildInputProperties() {
        List<String> result = new ArrayList<>();

        if (checker.hasOption("inputProperties")) {
            String[] props = checker.getOption("inputProperties").split(",");
            result.addAll(Arrays.asList(props));
        }

        return result;
    }

    /** Creates an AnnotationMirror for {@code @PolyDet} with {@code arg} as its value. */
    private AnnotationMirror newPolyDet(String arg) {
        AnnotationBuilder builder = new AnnotationBuilder(processingEnv, PolyDet.class);
        builder.setValue("value", arg);
        return builder.build();
    }

    @Override
    public QualifierPolymorphism createQualifierPolymorphism() {
        return new DeterminismQualifierPolymorphism(processingEnv, this);
    }

    @Override
    public CFTransfer createFlowTransferFunction(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        return new DeterminismTransfer((CFAnalysis) analysis);
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return new LinkedHashSet<>(
                Arrays.asList(
                        Det.class, OrderNonDet.class, NonDet.class, PolyDet.class, PolyAll.class));
    }

    @Override
    public TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(
                new DeterminismTreeAnnotator(this), super.createTreeAnnotator());
    }

    @Override
    protected TypeAnnotator createTypeAnnotator() {
        return new ListTypeAnnotator(
                super.createTypeAnnotator(),
                new DeterminismAnnotatedTypeFactory.DeterminismTypeAnnotator(this));
    }

    private class DeterminismTreeAnnotator extends TreeAnnotator {

        public DeterminismTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        /**
         * Refines the return type of a method invocation for the following cases:
         *
         * <ol>
         *   <li>Return type is a non-collection.
         *   <li>The invoked method is {@code equals} and the receiver is a {@code Set}.
         *   <li>The invoked method is {@code System.get}
         *   <li>The invoked method is {@code Map.get}
         * </ol>
         *
         * Replaces the annotation on the return type of a method invocation as follows:
         *
         * <ol>
         *   <li>If the annotation on the type of method invocation resolves to {@code OrderNonDet}
         *       and if the return type of the invoked method isn't an array or a collection,
         *       replaces the annotation on {@code methodInvocationType} with {@code @NonDet}.
         *   <li>Return type of equals() gets the annotation {@code @Det}, when both the receiver
         *       and the argument satisfy these conditions (@see <a
         *       href="https://checkerframework.org/manual/#determinism-improved-precision-set-equals">Improves
         *       precision for Set.equals()</a>):
         *       <ol>
         *         <li>the type is {@code @OrderNonDet Set}, and
         *         <li>its type argument is not {@code @OrderNonDet List} or a subtype
         *       </ol>
         *   <li>Annotates the return types of System.getProperty("line.separator") and
         *       System.getProperty("line.separator") as {@code Det}. Usually, the return type of
         *       System.getProperty() is annotated as {@code NonDet}. We make an exception when the
         *       argument is either {@code line.separator} or {@code path.separator} because they
         *       will always produce the same result on the same machine.
         * </ol>
         *
         * @param node method invocation tree
         * @param methodInvocationType type of the method invocation
         * @return visitMethodInvocation() of the super class
         */
        @Override
        public Void visitMethodInvocation(
                MethodInvocationTree node, AnnotatedTypeMirror methodInvocationType) {
            AnnotatedTypeMirror receiverType = getReceiverType(node);
            changeReturnOnNonCollections(methodInvocationType);

            // ReceiverType is null for abstract classes
            // (Example: Ordering.natural() in tests/all-systems/PolyCollectorTypeVars.java)
            // For static methods, receiverType is the AnnotatedTypeMirror of the class in which the
            // invoked method "node" is declared.
            if (receiverType == null) {
                return super.visitMethodInvocation(node, methodInvocationType);
            }

            TypeElement receiverUnderlyingType =
                    TypesUtils.getTypeElement(receiverType.getUnderlyingType());

            // Without this check, NullPointerException in Collections class with buildJdk.
            // Likely cause: Collections has a private constructor?
            // Error at line: public class Collections {
            // TODO-rashmi: check why?
            if (receiverUnderlyingType == null) {
                return super.visitMethodInvocation(node, methodInvocationType);
            }

            refineResultOfEquals(node, methodInvocationType, receiverType);
            refineSystemGet(node, methodInvocationType);
            refineMapGet(node, methodInvocationType, receiverType);

            return super.visitMethodInvocation(node, methodInvocationType);
        }

        /**
         * If the annotation on {@code methodInvocationType} is {@code OrderNonDet} and it isn't an
         * array or a collection, replaces the annotation on {@code methodInvocationType} with
         * {@code @NonDet}.
         *
         * @param methodInvocationType AnnotatedTypeMirror for a method invocation
         */
        protected void changeReturnOnNonCollections(AnnotatedTypeMirror methodInvocationType) {
            if (methodInvocationType.hasAnnotation(ORDERNONDET)
                    && !mayBeOrderNonDet(methodInvocationType)) {
                methodInvocationType.replaceAnnotation(NONDET);
            }
        }

        /**
         * Usually, the return type of {@code System.getProperty} is annotated as {@code NonDet}. We
         * make an exception when the argument is either {@code line.separator}, {@code
         * file.separator}, or {@code path.separator} because they will always produce the same
         * result on the same machine.
         *
         * @param node method invocation tree
         * @param methodInvocationType AnnotatedTypeMirror for a method invocation
         */
        private void refineSystemGet(
                MethodInvocationTree node, AnnotatedTypeMirror methodInvocationType) {
            ExecutableElement m = TreeUtils.elementFromUse(node);
            ExecutableElement systemGetProperty =
                    TreeUtils.getMethod("java.lang.System", "getProperty", 1, getProcessingEnv());
            if (ElementUtils.isMethod(m, systemGetProperty, getProcessingEnv())) {
                String getPropertyArgument = node.getArguments().get(0).toString();
                String getPropertyArgumentWithoutQuotes =
                        getPropertyArgument.substring(1, getPropertyArgument.length() - 1);
                if (getPropertyArgument.equals("\"" + "line.separator" + "\"")
                        || getPropertyArgument.equals("\"" + "file.separator" + "\"")
                        || getPropertyArgument.equals("\"" + "path.separator" + "\"")
                        || inputProperties.contains(getPropertyArgumentWithoutQuotes)) {
                    methodInvocationType.replaceAnnotation(DET);
                }
            }
        }

        /**
         * Return type of equals() gets the annotation {@code @Det}, when both the receiver and the
         * argument satisfy these conditions (@see <a
         * href="https://checkerframework.org/manual/#determinism-improved-precision-set-equals">Improves
         * precision for Set.equals()</a>):
         *
         * <ol>
         *   <li>the type is {@code @OrderNonDet Set}, and
         *   <li>its type argument is not {@code @OrderNonDet List} or a subtype
         * </ol>
         *
         * @param node method invocation tree
         * @param methodInvocationType AnnotatedTypeMirror for a method invocation
         * @param receiverType receiver type of the invoked method
         */
        protected void refineResultOfEquals(
                MethodInvocationTree node,
                AnnotatedTypeMirror methodInvocationType,
                AnnotatedTypeMirror receiverType) {

            // Annotates the return type of "equals()" method called on a Set receiver
            // as described in the specification of this method.

            // Example1: @OrderNonDet Set<@OrderNonDet List<@Det Integer>> s1;
            //           @OrderNonDet Set<@OrderNonDet List<@Det Integer>> s2;
            // s1.equals(s2) is @NonDet

            // Example 2: @OrderNonDet Set<@Det List<@Det Integer>> s1;
            //            @OrderNonDet Set<@Det List<@Det Integer>> s2;
            // s1.equals(s2) is @Det
            // TODO-rashmi: this can be more precise (@Det receiver and @OrderNonDet argument)

            if (isEqualsMethod(node)) {
                AnnotatedTypeMirror argument = getAnnotatedType(node.getArguments().get(0));
                if (isSubClassOf(receiverType, setInterfaceTypeMirror)
                        && receiverType.hasAnnotation(ORDERNONDET)
                        && !hasOrderNonDetListAsTypeArgument(receiverType)
                        && isSubClassOf(argument, setInterfaceTypeMirror)
                        && argument.hasAnnotation(ORDERNONDET)
                        && !hasOrderNonDetListAsTypeArgument(argument)) {
                    methodInvocationType.replaceAnnotation(DET);
                }
            }
        }

        /**
         * Since the return type of {@code Map.get} is annotated as {@code @PolyDet}, replace the
         * annotation on return type as {@code @Det} if the receiver is of type
         * {@code @OrderNonDet}, map's V(value) type argument of type {@code @Det}, and the argument
         * to {@code get} of type {@code @Det}.
         *
         * @param node method invocation tree
         * @param methodInvocationType AnnotatedTypeMirror for a method invocation
         * @param receiverType receiver type of the invoked method
         */
        private void refineMapGet(
                MethodInvocationTree node,
                AnnotatedTypeMirror methodInvocationType,
                AnnotatedTypeMirror receiverType) {
            if (isMap(receiverType)
                    && (isMapGet(node) || isMapGetOrDefault(node))
                    && receiverType.hasAnnotation(ORDERNONDET)
                    && ((AnnotatedDeclaredType) receiverType)
                            .getTypeArguments()
                            .get(1)
                            .hasAnnotation(DET)
                    && getAnnotatedType(node.getArguments().get(0)).hasAnnotation(DET)) {
                methodInvocationType.replaceAnnotation(DET);
            }
        }

        /** Returns true if the node is an invocation of Map.get. */
        boolean isMapGet(Tree tree) {
            return TreeUtils.isMethodInvocation(tree, mapGet, getProcessingEnv());
        }

        /** Returns true if the node is an invocation of Map.getOrDefault. */
        boolean isMapGetOrDefault(Tree tree) {
            return TreeUtils.isMethodInvocation(tree, mapGetOrDefault, getProcessingEnv());
        }

        /**
         * Annotates the length property of a {@code @NonDet} or a {@code @PolyDet} array as
         * {@code @NonDet} or {@code @PolyDet("down")} respectively.
         */
        @Override
        public Void visitMemberSelect(
                MemberSelectTree node, AnnotatedTypeMirror annotatedTypeMirror) {
            if (TreeUtils.isArrayLengthAccess(node)) {
                AnnotatedArrayType arrType =
                        (AnnotatedArrayType) getAnnotatedType(node.getExpression());
                if (arrType.hasAnnotation(NONDET)) {
                    annotatedTypeMirror.replaceAnnotation(NONDET);
                }
                if (arrType.hasAnnotation(POLYDET)) {
                    annotatedTypeMirror.replaceAnnotation(POLYDET_DOWN);
                }
            }
            return super.visitMemberSelect(node, annotatedTypeMirror);
        }

        /**
         * If {@code @Det} wasn't explicitly written on a {@code HashSet} or a {@code HashMap}, but
         * the constructor would resolve to {@code @Det}, inserts {@code @OrderNonDet} instead.
         *
         * <p>If {@code @OrderNonDet} wasn't explicitly written on a {@code TreeSet} or a {@code
         * TreeMap}, but the constructor would resolve to {@code @OrderNonDet}, inserts {@code @Det}
         * instead.
         *
         * @param node a tree representing instantiating a class
         * @param annotatedTypeMirror the type to modify if it represents an invalid constructor
         *     call
         * @return visitNewClass() of the super class
         */
        @Override
        public Void visitNewClass(NewClassTree node, AnnotatedTypeMirror annotatedTypeMirror) {
            if ((isHashSet(annotatedTypeMirror) && !isLinkedHashSet(annotatedTypeMirror))
                    || (isHashMap(annotatedTypeMirror) && !isLinkedHashMap(annotatedTypeMirror))) {
                if (annotatedTypeMirror.hasAnnotation(DET)) {
                    annotatedTypeMirror.replaceAnnotation(ORDERNONDET);
                }
            } else if (isTreeSet(annotatedTypeMirror) || isTreeMap(annotatedTypeMirror)) {
                if (annotatedTypeMirror.hasAnnotation(ORDERNONDET)) {
                    annotatedTypeMirror.replaceAnnotation(DET);
                }
            }
            return super.visitNewClass(node, annotatedTypeMirror);
        }
    }

    /**
     * Returns true if {@code @OrderNonDet List} appears as a top-level type argument in {@code
     * atm}.
     */
    private boolean hasOrderNonDetListAsTypeArgument(AnnotatedTypeMirror atm) {
        AnnotatedDeclaredType declaredType = (AnnotatedDeclaredType) atm;
        for (AnnotatedTypeMirror argType : declaredType.getTypeArguments()) {
            if (isSubClassOf(argType, listInterfaceTypeMirror)
                    && argType.hasAnnotation(ORDERNONDET)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if {@code javaType} may be annotated as {@code @OrderNonDet}.
     *
     * @param javaType the type to be checked
     * @return true if {@code javaType} is Collection (or a subtype), Iterator (or a subtype), or an
     *     array
     */
    public boolean mayBeOrderNonDet(AnnotatedTypeMirror javaType) {
        return (javaType.getKind() == TypeKind.ARRAY
                || isCollection(javaType)
                || isIterator(javaType)
                || isMap(javaType));
    }

    /**
     * Adds default annotations for:
     *
     * <ol>
     *   <li>component types of array parameters and returns.
     *   <li>return types of methods with no unannotated or @PolyDet formal parameters and receiver.
     * </ol>
     *
     * Adds implicit annotation for main method formal parameter.
     */
    protected class DeterminismTypeAnnotator extends TypeAnnotator {
        /** Calls the superclass constructor. */
        public DeterminismTypeAnnotator(DeterminismAnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        /**
         * Places the following default annotations:
         *
         * <ol>
         *   <li>Defaults the component types of array parameters and return types as {@code
         *       ...[@PolyDet]} in the body of the method represented by {@code executableType}.
         *   <li>Defaults the return type for methods with no @PolyDet formal parameters (including
         *       the receiver) as {@code @Det} in the method represented by {@code executableType}.
         * </ol>
         *
         * <p>NOTE: This method {@code visitExecutable} adds default types to parameter types inside
         * the method bodies, not in method signatures. The same defaults are added to method
         * signatures by {@code addComputedTypeAnnotations}.
         */
        @Override
        public Void visitExecutable(final AnnotatedExecutableType executableType, final Void p) {
            if (!isMainMethod(executableType.getElement())) {
                for (AnnotatedTypeMirror paramType : executableType.getParameterTypes()) {
                    defaultArrayComponentType(paramType, POLYDET);
                }

                if (executableType.getReturnType().getAnnotations().isEmpty()) {
                    boolean unannotatedOrPolyDet = false;
                    // First check receiver type. If it is unannotated or @PolyDet, don't check the
                    // rest of the parameters.
                    AnnotatedTypeMirror receiverType = executableType.getReceiverType();
                    if (receiverType != null) {
                        unannotatedOrPolyDet =
                                receiverType.getAnnotations().isEmpty()
                                        || receiverType.hasAnnotation(POLYDET);
                    }
                    if (!unannotatedOrPolyDet) {
                        for (AnnotatedTypeMirror paramType : executableType.getParameterTypes()) {
                            // The default is @PolyDet, so treat unannotated the same as @PolyDet.
                            // Note: "paramType.getAnnotations().isEmpty()" is true when there are
                            // no explicit annotations on "paramType".
                            if (paramType.getAnnotations().isEmpty()
                                    || paramType.hasAnnotation(POLYDET)) {
                                unannotatedOrPolyDet = true;
                                break;
                            }
                        }
                        if (!unannotatedOrPolyDet) {
                            executableType.getReturnType().replaceAnnotation(DET);
                        }
                    }
                    defaultArrayComponentType(executableType.getReturnType(), POLYDET);
                }
            }
            return super.visitExecutable(executableType, p);
        }
    }

    /**
     * If {@code type} is an array type that is not explicitly annotated, defaults all its nested
     * component types as {@code annotation}.
     */
    private void defaultArrayComponentType(AnnotatedTypeMirror type, AnnotationMirror annotation) {
        if (type.getKind() == TypeKind.ARRAY) {
            AnnotatedArrayType annoArrType = (AnnotatedArrayType) type;
            // The following code uses "annoArrType.getAnnotations().isEmpty()" and
            // "annoArrType.hasAnnotation(NONDET)" to check if 'annoArrType' has explicit
            // annotations.
            // It doesn't check for "annoArrType.getExplicitAnnotations().isEmpty()" or
            // "annoArrType.hasExplicitAnnotation(NONDET) because "getExplicitAnnotations()" works
            // only with type use locations.
            // For example: if 'annoArrType' is "@Det int @Det[]",
            // "arrParamType.getExplicitAnnotations().size()" returns 0,
            // "arrParamType.getAnnotations().size()" returns 1.
            // TODO: replace with getExplicitAnnotations() when Issue #2324 is fixed.
            // See https://github.com/typetools/checker-framework/issues/2324.
            if (annoArrType.getAnnotations().isEmpty() || annoArrType.hasAnnotation(NONDET)) {
                recursiveDefaultArrayComponentType(annoArrType, annotation);
            }
        }
    }

    /**
     * Defaults all the nested component types of the array type {@code annoArrType} as {@code
     * annotation}.
     *
     * <p>Example: If this method is called with {@code annoArrType} as {@code int[][]} and {@code
     * annotation} as {@code @PolyDet}, the resulting {@code annoArrType} will be {@code @PolyDet
     * int @PolyDet[][]}
     */
    void recursiveDefaultArrayComponentType(
            AnnotatedArrayType annoArrType, AnnotationMirror annotation) {
        AnnotatedTypeMirror componentType = annoArrType.getComponentType();
        if (!componentType.getAnnotations().isEmpty()) {
            return;
        }
        if (componentType.getUnderlyingType().getKind() != TypeKind.TYPEVAR) {
            componentType.replaceAnnotation(annotation);
        }
        if (componentType.getKind() != TypeKind.ARRAY) {
            return;
        }
        recursiveDefaultArrayComponentType((AnnotatedArrayType) componentType, annotation);
    }

    /**
     * If {@code type} is a Collection type that is not explicitly annotated, defaults all its
     * nested component types as {@code annotation}.
     */
    void defaultCollectionComponentType(AnnotatedTypeMirror type, AnnotationMirror annotation) {
        if ((isCollection(type) || isMap(type) || isIterator(type))
                && (type.getAnnotations().isEmpty() || type.hasExplicitAnnotation(NONDET))) {
            AnnotatedDeclaredType annoCollectionType = (AnnotatedDeclaredType) type;
            recursiveDefaultCollectionComponentType(annoCollectionType, annotation);
        }
    }

    /**
     * Defaults all the nested component types of the collection type {@code type} as {@code
     * annotation}.
     *
     * <p>Example: If this method is called with {@code annoArrType} as {@code List<List<Integer>>}
     * and {@code annotation} as {@code @PolyDet}, the resulting {@code type} will be {@code
     * List<@PolyDet List<@PolyDet Integer>>}
     */
    private void recursiveDefaultCollectionComponentType(
            AnnotatedDeclaredType type, AnnotationMirror annotation) {
        for (AnnotatedTypeMirror argType : type.getTypeArguments()) {
            if (argType.getKind() != TypeKind.TYPEVAR
                    && argType.getKind() != TypeKind.WILDCARD
                    && argType.getAnnotations().isEmpty()) {
                argType.replaceAnnotation(annotation);
                if (isCollection(argType) || isMap(argType) || isIterator(argType)) {
                    recursiveDefaultCollectionComponentType(
                            (AnnotatedDeclaredType) argType, annotation);
                }
            }
        }
    }

    /** Returns true if the node is an invocation of Object.equals. */
    boolean isEqualsMethod(Tree tree) {
        return TreeUtils.isMethodInvocation(tree, equals, getProcessingEnv());
    }

    /** @return true if {@code method} is a main method */
    public static boolean isMainMethod(ExecutableElement method) {
        if (method.getReturnType().getKind() == TypeKind.VOID
                && method.getSimpleName().contentEquals("main")
                && method.getParameters().size() == 1
                && method.getParameters().get(0).asType().getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) method.getParameters().get(0).asType();
            if (TypesUtils.isString(arrayType.getComponentType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds implicit annotation for main method formal parameter ({@code @Det}) and default
     * annotations for the component types of other array formal parameters ({@code ...[@PolyDet]}).
     *
     * <p>Note: The annotation on an array type defaults to {@code @PolyDet[]} and this defaulting
     * is handled by declarative mechanism.
     *
     * <p>Example: Consider the following code:
     *
     * <pre><code>
     * &nbsp; void testArr(int[] a) {
     * &nbsp;   ...
     * &nbsp; }
     * </code></pre>
     *
     * This method {@code addComputedTypeAnnotations} annotates the component type of parameter
     * {@code int[] a} as {@code @PolyDet int[] a}.
     *
     * <p>Note: Even though {@code visitExecutable} and {@code addComputedTypeAnnotations} have the
     * same logic for adding defaults to parameter types, the code structure is different. This is
     * because the argument to {@code visitExecutable} is an {@code AnnotatedExecutableType} which
     * represents the type of a method, constructor, or initializer, and the argument to {@code
     * addComputedTypeAnnotations} is any {@code Element}.
     */
    @Override
    public void addComputedTypeAnnotations(Element elt, AnnotatedTypeMirror type) {
        if (elt.getKind() == ElementKind.PARAMETER) {
            if (elt.getEnclosingElement().getKind() == ElementKind.METHOD) {
                ExecutableElement method = (ExecutableElement) elt.getEnclosingElement();
                if (isMainMethod(method)) {
                    if (!type.getAnnotations().isEmpty() && !type.hasAnnotation(DET)) {
                        checker.report(
                                Result.failure(
                                        "invalid.annotation.on.parameter",
                                        type.getAnnotationInHierarchy(NONDET)),
                                elt);
                    }
                    type.addMissingAnnotations(Collections.singleton(DET));
                } else if (isList(type.getUnderlyingType())
                        && !type.isAnnotatedInHierarchy(NONDET)) {

                    type.addAnnotation(DET);
                    defaultCollectionComponentType(type, DET);

                } else {
                    defaultArrayComponentType(type, POLYDET);
                }
            } else if (elt.getEnclosingElement().getKind() == ElementKind.CONSTRUCTOR) {
                type.addMissingAnnotations(Collections.singleton(DET));
            }
        }
        if (elt.getKind() == ElementKind.LOCAL_VARIABLE) {
            if (isList(type.getUnderlyingType()) && !type.isAnnotatedInHierarchy(NONDET)) {
                type.addAnnotation(DET);
                defaultCollectionComponentType(type, DET);
            } else {
                defaultArrayComponentType(type, NONDET);
                defaultCollectionComponentType(type, NONDET);
            }
        }
        super.addComputedTypeAnnotations(elt, type);
    }

    @Override
    protected void addComputedTypeAnnotations(
            Tree tree, AnnotatedTypeMirror type, boolean iUseFlow) {
        Element elt = TreeUtils.elementFromTree(tree);
        if (elt != null
                && elt.getKind() == ElementKind.LOCAL_VARIABLE
                && tree.getKind() == Tree.Kind.VARIABLE) {
            if (isList(type.getUnderlyingType()) && !type.isAnnotatedInHierarchy(NONDET)) {
                type.addAnnotation(DET);
                defaultCollectionComponentType(type, DET);

            } else {
                defaultArrayComponentType(type, NONDET);
                defaultCollectionComponentType(type, NONDET);
            }
        }
        super.addComputedTypeAnnotations(tree, type, iUseFlow);
    }

    @Override
    public void postAsMemberOf(
            AnnotatedTypeMirror type, AnnotatedTypeMirror owner, Element element) {
        super.postAsMemberOf(type, owner, element);
        if (element.getKind() == ElementKind.FIELD && !owner.hasAnnotation(DET)) {
            if (owner.hasAnnotation(POLYDET) && type.hasAnnotation(POLYDET)) {
                // skip
            } else {
                type.replaceAnnotation(NONDET);
            }
        }
    }

    /** @return true if {@code subClass} is a subtype of {@code superClass} */
    private boolean isSubClassOf(AnnotatedTypeMirror subClass, TypeMirror superClass) {
        return types.isSubtype(
                types.erasure(subClass.getUnderlyingType()), types.erasure(superClass));
    }

    /** @return true if {@code tm} is Collection or a subtype of Collection */
    public boolean isCollection(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()),
                types.erasure(collectionInterfaceTypeMirror));
    }

    /** @return true if {@code tm} is Map or a subtype of Map */
    public boolean isMap(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(mapInterfaceTypeMirror));
    }

    /** @return true if {@code tm} is Iterator or a subtype of Iterator */
    public boolean isIterator(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(iteratorTypeMirror));
    }

    /** @return true if {@code tm} is a HashSet or a subtype of HashSet */
    public boolean isHashSet(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(hashSetTypeMirror));
    }

    /** @return true if {@code tm} is a LinkedHashSet or a subtype of LinkedHashSet */
    public boolean isLinkedHashSet(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(linkedHashSetTypeMirror));
    }

    /** @return true if {@code tm} is a TreeSet or a subtype of TreeSet */
    public boolean isTreeSet(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(treeSetTypeMirror));
    }

    /** @return true if {@code tm} is a List or a subtype of List */
    public boolean isList(TypeMirror tm) {
        return types.isSubtype(types.erasure(tm), types.erasure(listInterfaceTypeMirror));
    }

    /** @return true if {@code tm} is a HashMap or a subtype of HashMap */
    public boolean isHashMap(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(hashMapTypeMirror));
    }

    /** @return true if {@code tm} is a LinkedHashMap or a subtype of LinkedHashMap */
    public boolean isLinkedHashMap(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(linkedHashMapTypeMirror));
    }

    /** @return true if {@code tm} is a TreeMap or a subtype of TreeMap */
    public boolean isTreeMap(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(treeMapTypeMirror));
    }

    /** @return true if {@code tm} is the Arrays class */
    public boolean isArrays(TypeMirror tm) {
        return types.isSameType(tm, arraysTypeMirror);
    }

    /** @return true if {@code tm} is the Collections class */
    public boolean isCollections(TypeMirror tm) {
        return types.isSameType(tm, collectionsTypeMirror);
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy(
            MultiGraphQualifierHierarchy.MultiGraphFactory factory) {
        return new DeterminismQualifierHierarchy(factory, DET);
    }

    /**
     * Returns the annotation placed on the constructor in {@code tree} if it represents
     * constructing a parameterized type such as {@code HashSet}.
     *
     * @param tree the {@code Tree} representing the construction of a new parameterized type such
     *     as {@code HashSet}.
     * @return the annotation in the determinism hierarchy on the given constructor if there was
     *     one, {@code null} otherwise.
     */
    public AnnotationMirror getNewClassAnnotation(NewClassTree tree) {
        return fromNewClass(tree).getAnnotationInHierarchy(NONDET);
    }

    class DeterminismQualifierHierarchy extends GraphQualifierHierarchy {

        public DeterminismQualifierHierarchy(MultiGraphFactory f, AnnotationMirror bottom) {
            super(f, bottom);
        }

        /**
         * Adds the following subtyping rules for {@code @PolyDet}:
         *
         * <ol>
         *   <li>{@code @PolyDet("down")} <: {@code @PolyDet} <: {@code @PolyDet("up")}.
         *   <li>{@code @PolyDet("use")} and {@code @PolyDet} are considered to be equal.
         *   <li>Treats {@code @PolyDet("up")} and {@code @PolyDet("down")} as {@code @PolyDet} when
         *       they are compared with {@code @NonDet}, {@code @OrderNonDet}, or {@code @Det}.
         * </ol>
         */
        @Override
        public boolean isSubtype(AnnotationMirror subAnno, AnnotationMirror superAnno) {
            if (AnnotationUtils.areSame(subAnno, POLYDET)
                    && AnnotationUtils.areSame(superAnno, POLYDET_UP)) {
                return true;
            }
            if (AnnotationUtils.areSame(subAnno, POLYDET_UP)
                    && AnnotationUtils.areSame(superAnno, POLYDET)) {
                return false;
            }
            if (AnnotationUtils.areSame(subAnno, POLYDET_DOWN)
                    && AnnotationUtils.areSame(superAnno, POLYDET)) {
                return true;
            }
            if (AnnotationUtils.areSame(subAnno, POLYDET)
                    && AnnotationUtils.areSame(superAnno, POLYDET_DOWN)) {
                return false;
            }
            if (AnnotationUtils.areSame(subAnno, POLYDET_DOWN)
                    && AnnotationUtils.areSame(superAnno, POLYDET_UP)) {
                return true;
            }
            if (AnnotationUtils.areSame(subAnno, POLYDET_UP)
                    && AnnotationUtils.areSame(superAnno, POLYDET_DOWN)) {
                return false;
            }
            if (AnnotationUtils.areSameByName(subAnno, POLYDET)) {
                subAnno = POLYDET;
            }
            if (AnnotationUtils.areSameByName(superAnno, POLYDET)) {
                superAnno = POLYDET;
            }
            return super.isSubtype(subAnno, superAnno);
        }
    }
}
