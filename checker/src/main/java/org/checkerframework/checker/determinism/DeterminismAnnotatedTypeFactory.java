package org.checkerframework.checker.determinism;

import com.sun.source.tree.*;
import java.lang.annotation.Annotation;
import java.util.*;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.CollectionType;
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
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedWildcardType;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.poly.QualifierPolymorphism;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.framework.type.visitor.SimpleAnnotatedTypeScanner;
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
    /** The @PolyDet("upDet") annotation. */
    public final AnnotationMirror POLYDET_UPDET;
    /** The @PolyDet("noOrderNonDet") annotation. */
    public final AnnotationMirror POLYDET_NOORDERNONDET;
    /** The @PolyDet("useNoOrderNonDet") annotation. */
    public final AnnotationMirror POLYDET_USENOORDERNONDET;

    /** The java.util.Set interface. */
    private final TypeMirror setInterfaceTypeMirror =
            TypesUtils.typeFromClass(Set.class, types, processingEnv.getElementUtils());
    /** The java.util.List interface. */
    private final TypeMirror listInterfaceTypeMirror =
            TypesUtils.typeFromClass(List.class, types, processingEnv.getElementUtils());
    /** The java.util.Map class. */
    private final TypeMirror mapInterfaceTypeMirror =
            TypesUtils.typeFromClass(Map.class, types, processingEnv.getElementUtils());;
    /** The java.util.Arrays class. */
    private final TypeMirror arraysTypeMirror =
            TypesUtils.typeFromClass(Arrays.class, types, processingEnv.getElementUtils());
    /** The java.util.Collections class. */
    private final TypeMirror collectionsTypeMirror =
            TypesUtils.typeFromClass(Collections.class, types, processingEnv.getElementUtils());
    /** The java.util.TreeSet class. */
    private final TypeMirror treeSetTypeMirror =
            TypesUtils.typeFromClass(TreeSet.class, types, processingEnv.getElementUtils());
    /** The java.util.TreeMap class. */
    private final TypeMirror treeMapTypeMirror =
            TypesUtils.typeFromClass(TreeMap.class, types, processingEnv.getElementUtils());
    /** The java.util.HashSet class. */
    private final TypeMirror hashSetTypeMirror =
            TypesUtils.typeFromClass(HashSet.class, types, processingEnv.getElementUtils());
    /** The java.util.HashMap class. */
    private final TypeMirror hashMapTypeMirror =
            TypesUtils.typeFromClass(HashMap.class, types, processingEnv.getElementUtils());

    /** Comma separated list of deterministic system properties */
    private final List<String> inputProperties;

    /** The Map.get method. */
    private final ExecutableElement mapGet;

    /** The Map.getOrDefault method. */
    private final ExecutableElement mapGetOrDefault;

    /** The Object.equals method. */
    private final ExecutableElement equals;

    /**
     * Creates {@code @PolyDet} annotation mirror constants.
     *
     * @param checker BaseTypeChecker
     */
    public DeterminismAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);

        POLYDET = newPolyDet("");
        POLYDET_UP = newPolyDet("up");
        POLYDET_DOWN = newPolyDet("down");
        POLYDET_USE = newPolyDet("use");
        POLYDET_UPDET = newPolyDet("upDet");
        POLYDET_NOORDERNONDET = newPolyDet("noOrderNonDet");
        POLYDET_USENOORDERNONDET = newPolyDet("useNoOrderNonDet");

        this.inputProperties = Collections.unmodifiableList(buildInputProperties());

        mapGet = TreeUtils.getMethod("java.util.Map", "get", 1, processingEnv);
        mapGetOrDefault = TreeUtils.getMethod("java.util.Map", "getOrDefault", 2, processingEnv);
        equals = TreeUtils.getMethod("java.lang.Object", "equals", 1, processingEnv);

        postInit();
    }

    /**
     * Returns a list of properties supplied by the user via the command line option
     * "-AinputProperties".
     *
     * @return List of properties
     */
    private List<String> buildInputProperties() {
        List<String> result = new ArrayList<>();

        if (checker.hasOption("inputProperties")) {
            String[] props = checker.getOption("inputProperties").split(",");
            result.addAll(Arrays.asList(props));
        }

        return result;
    }

    /**
     * Creates an AnnotationMirror for {@code @PolyDet} with {@code arg} as its value.
     *
     * @param arg String value
     * @return the created AnnotationMirror
     */
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
                Arrays.asList(Det.class, OrderNonDet.class, NonDet.class, PolyDet.class));
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

    /** TreeAnnotator for the Determinism checker. */
    private class DeterminismTreeAnnotator extends TreeAnnotator {

        /**
         * DeterminismTreeAnnotator constructor.
         *
         * @param atypeFactory AnnotatedTypeFactory
         */
        public DeterminismTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        /**
         * Refines the type of a method invocation for the following cases:
         *
         * <ol>
         *   <li>Replaces {@code @OrderNonDet} with {@code @NonDet} if the Java type isn't a
         *       collection
         *   <li>Type is {@code @PolyDet("up")}
         *   <li>The invoked method is {@code equals} and the receiver is a {@code Set} or {@code
         *       Map}.
         *   <li>The invoked method is {@code System.get}
         *   <li>The invoked method is {@code Map.get}
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
            ExecutableElement m = TreeUtils.elementFromUse(node);

            if (methodInvocationType.hasAnnotation(ORDERNONDET)
                    && !isCollectionType(methodInvocationType)) {
                methodInvocationType.replaceAnnotation(NONDET);
            }
            refinePolyUp(node, methodInvocationType, receiverType, m);

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

            // Annotates the return type of "equals()" method called on a Set or Map receiver
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
                boolean bothSets =
                        isSubClassOf(receiverType, setInterfaceTypeMirror)
                                && isSubClassOf(argument, setInterfaceTypeMirror);
                boolean bothMaps =
                        isSubClassOf(receiverType, mapInterfaceTypeMirror)
                                && isSubClassOf(argument, mapInterfaceTypeMirror);
                if ((bothSets || bothMaps)
                        && getQualifierHierarchy()
                                .isSubtype(
                                        receiverType.getAnnotationInHierarchy(NONDET), ORDERNONDET)
                        && !hasOrderNonDetListAsTypeArgument(receiverType)
                        && getQualifierHierarchy()
                                .isSubtype(argument.getAnnotationInHierarchy(NONDET), ORDERNONDET)
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
            if ((isMapGet(node) || isMapGetOrDefault(node))
                    && receiverType.hasAnnotation(ORDERNONDET)
                    && ((AnnotatedDeclaredType) receiverType)
                            .getTypeArguments()
                            .get(1)
                            .hasAnnotation(DET)
                    && getAnnotatedType(node.getArguments().get(0)).hasAnnotation(DET)) {
                methodInvocationType.replaceAnnotation(DET);
            }
        }

        /**
         * Returns true if the node is an invocation of Map.get.
         *
         * @param tree Tree
         * @return Returns true if the node is an invocation of Map.get
         */
        boolean isMapGet(Tree tree) {
            return TreeUtils.isMethodInvocation(tree, mapGet, getProcessingEnv());
        }

        /**
         * Returns true if the node is an invocation of Map.getOrDefault.
         *
         * @param tree Tree
         * @return Returns true if the node is an invocation of Map.getOrDefault
         */
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
         * When an array of type {@code @OrderNonDet} (or {@code @PolyDet}) is accessed on the rhs,
         * this method annotates the type of the array access expression (equivalently, the array
         * element) as {@code @NonDet} (or {@code @PolyDet("up")}). Example:
         *
         * <pre><code>
         * &nbsp; @Det int @OrderNonDet [] arr;
         * &nbsp; int val = arr[0];
         * </code></pre>
         *
         * In the code above, type of arr[0] gets annotated as {@code @NonDet}.
         *
         * <p>This method also annotates the type of an rhs array expression as {@code @NonDet} (or
         * {@code @PolyDet}) if the index type is annotated as {@code @NonDet} (or
         * {@code @PolyDet}). Example:
         *
         * <pre><code>
         * &nbsp; @Det int @Det [] arr;
         * &nbsp; @NonDet int index;
         * &nbsp; int val = arr[index];
         * </code></pre>
         *
         * In the code above, type of arr[index] gets annotated as {@code @NonDet}.
         *
         * @param node the annotated type of the variable
         * @param annotatedTypeMirror the annotated type of the value
         * @checker_framework.manual #﻿determinism-access-array-elements Access array elements
         */
        @Override
        public Void visitArrayAccess(
                ArrayAccessTree node, AnnotatedTypeMirror annotatedTypeMirror) {
            if (!isLHS) {
                AnnotationMirror arrTopType =
                        atypeFactory
                                .getAnnotatedType(node.getExpression())
                                .getAnnotationInHierarchy(NONDET);
                AnnotationMirror indextype =
                        atypeFactory
                                .getAnnotatedType(node.getIndex())
                                .getAnnotationInHierarchy(NONDET);
                if (AnnotationUtils.areSame(arrTopType, ORDERNONDET)
                        || AnnotationUtils.areSame(arrTopType, NONDET)
                        || AnnotationUtils.areSame(indextype, NONDET)) {
                    annotatedTypeMirror.replaceAnnotation(NONDET);
                } else if (AnnotationUtils.areSameByName(arrTopType, POLYDET)) {
                    annotatedTypeMirror.replaceAnnotation(POLYDET_UP);
                } else if (AnnotationUtils.areSame(indextype, POLYDET)) {
                    annotatedTypeMirror.replaceAnnotation(POLYDET);
                }
            }
            return super.visitArrayAccess(node, annotatedTypeMirror);
        }
    }

    /**
     * If the return type is {@code @PolyDet("up")}, and if this was because a method that returns
     * {@code @PolyDet("up")} was passed a {@code @PolyDet} argument, but no such {@code @PolyDet}
     * argument could be {@code @OrderNonDet}, then changes the return type to {@code @PolyDet}.
     * This is because {@code @PolyDet("up")} is imprecise if no {@code @PolyDet} argument could be
     * {@code @OrderNonDet}. replaces the annotation on {@code methodInvocationType} with
     * {@code @NonDet}.
     *
     * @param node MethodInvocationTree
     * @param methodInvocationType AnnotatedTypeMirror
     * @param receiverType AnnotatedTypeMirror
     * @param m ExecutableElement
     */
    private void refinePolyUp(
            MethodInvocationTree node,
            AnnotatedTypeMirror methodInvocationType,
            AnnotatedTypeMirror receiverType,
            ExecutableElement m) {
        // Makes a @PolyDet("up") return type more precise if possible. If the method call has
        // at least one @PolyDet argument and no @PolyDet argument that can be @OrderNonDet,
        // then the return type is changed to @PolyDet. This is because if no @PolyDet parameter
        // can be @OrderNonDet, then it should never be the return type is @PolyDet("up").
        // However, if there is no @PolyDet argument then this refinement would be invalid.
        if (methodInvocationType.hasAnnotation(POLYDET_UP)) {
            boolean hasPolyArg = false;
            boolean hasPolyONDArg = false;
            for (ExpressionTree argTree : node.getArguments()) {
                AnnotatedTypeMirror argType = getAnnotatedType(argTree);
                if (argType.hasAnnotation(POLYDET)) {
                    hasPolyArg = true;
                    if (isCollectionType(argType)) {
                        hasPolyONDArg = true;
                        break;
                    }
                }
            }
            // If receiverType is null then this is a static method and the receiver should be
            // ignored.
            if (receiverType != null
                    && !ElementUtils.isStatic(m)
                    && receiverType.hasAnnotation(POLYDET)) {
                hasPolyArg = true;
                if (isCollectionType(receiverType)) {
                    hasPolyONDArg = true;
                }
            }
            if (hasPolyArg && !hasPolyONDArg) {
                methodInvocationType.replaceAnnotation(POLYDET);
            }
        }
    }

    /**
     * @param atm AnnotatedTypeMirror
     * @return true if {@code @OrderNonDet List} appears as a top-level type argument in {@code atm}
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
     * Returns true if the underlying type of {@code atm} or any of its super types is annotated
     * with {@link CollectionType}.
     *
     * @param atm annotated type mirror
     * @return true if the underlying type of {@code atm} is a collection type
     */
    public boolean isCollectionType(AnnotatedTypeMirror atm) {
        if (atm.getKind() == TypeKind.ARRAY) {
            return true;
        }
        TypeElement typeElement = TypesUtils.getTypeElement(atm.getErased().getUnderlyingType());
        return getDeclAnnotation(typeElement, CollectionType.class) != null;
    }

    /**
     * Replaces type of array components in the extends clause as {@code @NonDet}. Without this
     * replacement, calling {@code getClass()} on an array returns the type {@code Class<? extends
     * Det Object @NonDet[]>} which is an invalid type.
     *
     * @param getClassType type representing a call to Object.getClass
     * @param receiverType the receiver type of the method invocation
     */
    @Override
    public void adaptGetClassReturnTypeToReceiver(
            AnnotatedExecutableType getClassType, AnnotatedTypeMirror receiverType) {

        super.adaptGetClassReturnTypeToReceiver(getClassType, receiverType);

        AnnotatedDeclaredType returnAdt = (AnnotatedDeclaredType) getClassType.getReturnType();
        List<AnnotatedTypeMirror> typeArgs = returnAdt.getTypeArguments();
        AnnotatedWildcardType classWildcardArg = (AnnotatedWildcardType) typeArgs.get(0);
        if (classWildcardArg.getExtendsBoundField().getKind() == TypeKind.ARRAY) {
            AnnotatedTypeMirror extendsBoundArray = classWildcardArg.getExtendsBoundField();
            new AnnotationReplacer().visit(extendsBoundArray, NONDET);
        }
    }

    /** Replaces the annotation on {@code type} with {@code annotationMirror} */
    static class AnnotationReplacer extends SimpleAnnotatedTypeScanner<Void, AnnotationMirror> {
        @Override
        protected Void defaultAction(AnnotatedTypeMirror type, AnnotationMirror annotationMirror) {
            type.replaceAnnotation(annotationMirror);
            return super.defaultAction(type, annotationMirror);
        }
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
        /**
         * Calls the superclass constructor.
         *
         * @param atypeFactory DeterminismAnnotatedTypeFactory
         */
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
                    if (receiverType != null
                            && executableType.getElement().getKind() != ElementKind.CONSTRUCTOR) {
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
     *
     * @param type AnnotatedTypeMirror
     * @param annotation AnnotationMirror
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
     *
     * @param annoArrType AnnotatedArrayType
     * @param annotation AnnotationMirror
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
     *
     * @param type annotated type
     * @param annotation annotation to add to the type
     */
    void defaultCollectionComponentType(AnnotatedTypeMirror type, AnnotationMirror annotation) {
        if ((isCollectionType(type) && type.getKind() != TypeKind.ARRAY)
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
     *
     * @param type AnnotatedDeclaredType
     * @param annotation AnnotationMirror
     */
    private void recursiveDefaultCollectionComponentType(
            AnnotatedDeclaredType type, AnnotationMirror annotation) {
        for (AnnotatedTypeMirror argType : type.getTypeArguments()) {
            if (argType.getKind() != TypeKind.TYPEVAR
                    && argType.getKind() != TypeKind.WILDCARD
                    && argType.getAnnotations().isEmpty()) {
                argType.replaceAnnotation(annotation);
                if (isCollectionType(argType) && argType.getKind() != TypeKind.ARRAY) {
                    recursiveDefaultCollectionComponentType(
                            (AnnotatedDeclaredType) argType, annotation);
                }
            }
        }
    }

    /**
     * @param tree Tree
     * @return true if the node is an invocation of Object.equals
     */
    boolean isEqualsMethod(Tree tree) {
        return TreeUtils.isMethodInvocation(tree, equals, getProcessingEnv());
    }

    /**
     * @param method ExecutableElement
     * @return true if {@code method} is a main method
     */
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
                        checker.reportError(
                                elt,
                                "invalid.annotation.on.parameter",
                                type.getAnnotationInHierarchy(NONDET));
                    }
                    type.addMissingAnnotations(Collections.singleton(DET));
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
        // For the field access of "element" whose type is "type" and whose access expression's type
        // is "owner".
        if (!isLHS
                && owner.getKind() != TypeKind.ARRAY // array.length is dealt with elsewhere
                && element.getKind() == ElementKind.FIELD
                && !ElementUtils.isStatic(element)) {
            // The qualifier type of a field access is the LUB of the qualifier on the type of the
            // field and the qualifier on the type of the access expression.
            AnnotationMirror expressionAnno = owner.getEffectiveAnnotationInHierarchy(NONDET);
            AnnotationMirror fieldAnno = type.getEffectiveAnnotationInHierarchy(NONDET);
            type.replaceAnnotation(qualHierarchy.leastUpperBound(expressionAnno, fieldAnno));
        }
    }

    /** Is the type of a left hand side currently being computed? */
    private boolean isLHS = false;

    @Override
    public AnnotatedTypeMirror getAnnotatedTypeLhs(Tree lhsTree) {
        boolean oldIsLhs = isLHS;
        isLHS = true;
        AnnotatedTypeMirror type = super.getAnnotatedTypeLhs(lhsTree);
        isLHS = oldIsLhs;
        return type;
    }

    /**
     * @param subClass subClass AnnotatedTypeMirror
     * @param superClass superClass AnnotatedTypeMirror
     * @return true if {@code subClass} is a subtype of {@code superClass}
     */
    private boolean isSubClassOf(AnnotatedTypeMirror subClass, TypeMirror superClass) {
        return types.isSubtype(
                types.erasure(subClass.getUnderlyingType()), types.erasure(superClass));
    }

    /**
     * Returns true if {@code tm} is a TreeSet or a subtype of TreeSet
     *
     * @param tm AnnotatedTypeMirror
     * @return true if {@code tm} is a TreeSet or a subtype of TreeSet
     */
    public boolean isTreeSet(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(treeSetTypeMirror));
    }

    /**
     * Returns true if {@code tm} is a HashSet
     *
     * @param tm AnnotatedTypeMirror
     * @return true if {@code tm} is a HashSet
     */
    public boolean isHashSet(AnnotatedTypeMirror tm) {
        return types.isSameType(
                types.erasure(tm.getUnderlyingType()), types.erasure(hashSetTypeMirror));
    }

    /**
     * @param tm TypeMirror
     * @return true if {@code tm} is a List or a subtype of List
     */
    public boolean isList(TypeMirror tm) {
        return types.isSubtype(types.erasure(tm), types.erasure(listInterfaceTypeMirror));
    }

    /**
     * @param tm AnnotatedTypeMirror
     * @return true if {@code tm} is a TreeMap or a subtype of TreeMap
     */
    public boolean isTreeMap(AnnotatedTypeMirror tm) {
        return types.isSubtype(
                types.erasure(tm.getUnderlyingType()), types.erasure(treeMapTypeMirror));
    }

    /**
     * Returns true if {@code tm} is a HashMap
     *
     * @param tm AnnotatedTypeMirror
     * @return true if {@code tm} is a HashMap
     */
    public boolean isHashMap(AnnotatedTypeMirror tm) {
        return types.isSameType(
                types.erasure(tm.getUnderlyingType()), types.erasure(hashMapTypeMirror));
    }

    /**
     * @param tm TypeMirror
     * @return true if {@code tm} is the Arrays class
     */
    public boolean isArrays(TypeMirror tm) {
        return types.isSameType(tm, arraysTypeMirror);
    }

    /**
     * @param tm TypeMirror
     * @return true if {@code tm} is the Collections class
     */
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

    /** Defines LUB and subtyping relationships. */
    class DeterminismQualifierHierarchy extends GraphQualifierHierarchy {

        /**
         * DeterminismQualifierHierarchy constructor.
         *
         * @param f MultiGraphFactory
         * @param bottom AnnotationMirror
         */
        public DeterminismQualifierHierarchy(MultiGraphFactory f, AnnotationMirror bottom) {
            super(f, bottom);
        }

        /**
         * LUB of {@code @PolyDet("down")} and {@code @PolyDet("noOrderNonDet")} is {@code @PolyDet}
         * since they are unrelated to each other and are subtypes of {@code @PolyDet}.
         */
        @Override
        public AnnotationMirror leastUpperBound(AnnotationMirror a1, AnnotationMirror a2) {
            if ((AnnotationUtils.areSame(a1, POLYDET_DOWN)
                            && AnnotationUtils.areSame(a2, POLYDET_NOORDERNONDET))
                    || (AnnotationUtils.areSame(a1, POLYDET_NOORDERNONDET)
                            && AnnotationUtils.areSame(a2, POLYDET_DOWN))) {
                return POLYDET;
            }
            return super.leastUpperBound(a1, a2);
        }

        /**
         * Adds the following subtyping rules for {@code @PolyDet}:
         *
         * <ol>
         *   <li>{@code @PolyDet("down")} {@literal <}: {@code @PolyDet} {@literal <}:
         *       {@code @PolyDet("up")}.
         *   <li>{@code @PolyDet} {@literal <}: {@code @PolyDet("upDet")}.
         *   <li>{@code @PolyDet("noOrderNonDet")} {@literal <}: {@code @PolyDet}.
         *   <li>{@code @PolyDet("use")} and {@code @PolyDet} are considered to be equal.
         *   <li>{@code @PolyDet("useNoOrderNonDet")} and {@code @PolyDet("noOrderNonDet")} are also
         *       considered to be equal.
         *   <li>{@code @PolyDet("upDet")} {@literal <}: {@code @OrderNonDet}.
         *   <li>Treats {@code @PolyDet("up")} and {@code @PolyDet("down")} as {@code @PolyDet} when
         *       they are compared with {@code @NonDet}, {@code @OrderNonDet}, or {@code @Det}.
         * </ol>
         */
        @Override
        public boolean isSubtype(AnnotationMirror subAnno, AnnotationMirror superAnno) {
            if (!AnnotationUtils.areSameByClass(subAnno, PolyDet.class)
                    && !AnnotationUtils.areSameByClass(superAnno, PolyDet.class)) {
                return super.isSubtype(subAnno, superAnno);
            }
            if (AnnotationUtils.areSameByClass(subAnno, PolyDet.class)
                    && !AnnotationUtils.areSameByClass(superAnno, PolyDet.class)) {
                if (AnnotationUtils.areSame(subAnno, POLYDET_UPDET)
                        && AnnotationUtils.areSame(superAnno, ORDERNONDET)) {
                    return false;
                }
                return super.isSubtype(POLYDET, superAnno);
            }
            if (!AnnotationUtils.areSameByClass(subAnno, PolyDet.class)
                    && AnnotationUtils.areSameByClass(superAnno, PolyDet.class)) {
                if (AnnotationUtils.areSame(subAnno, ORDERNONDET)
                        && AnnotationUtils.areSame(superAnno, POLYDET_UPDET)) {
                    return true;
                }
                return super.isSubtype(subAnno, POLYDET);
            }
            String subAnnoValue =
                    AnnotationUtils.getElementValue(subAnno, "value", String.class, true);
            String superAnnoValue =
                    AnnotationUtils.getElementValue(superAnno, "value", String.class, true);
            switch (subAnnoValue) {
                case "":
                case "use":
                    switch (superAnnoValue) {
                        case "down":
                        case "noOrderNonDet":
                        case "useNoOrderNonDet":
                            return false;
                        default:
                            return true;
                    }
                case "up":
                    switch (superAnnoValue) {
                        case "":
                        case "down":
                        case "upDet":
                        case "noOrderNonDet":
                        case "use":
                        case "useNoOrderNonDet":
                            return false;
                        default:
                            return true;
                    }
                case "down":
                    return !"noOrderNonDet".equals(superAnnoValue);
                case "upDet":
                    switch (superAnnoValue) {
                        case "":
                        case "up":
                        case "down":
                        case "noOrderNonDet":
                        case "use":
                        case "useNoOrderNonDet":
                            return false;
                        default:
                            return true;
                    }
                case "noOrderNonDet":
                case "useNoOrderNonDet":
                    return !"down".equals(superAnnoValue);
                default:
                    throw new BugInCF(
                            "Subtyping relationship not defined for %s and %s", subAnno, superAnno);
            }
        }
    }
    /**
     * {@inheritDoc}
     *
     * <p>Defaults all fields in a class with an implicit qualifier parameter to {@code PolyDet}.
     *
     * @param elt Element whose type is {@code type}
     * @param type where the defaults are applied
     */
    @Override
    protected void applyQualifierParameterDefaults(Element elt, AnnotatedTypeMirror type) {
        if (elt == null
                || elt.getKind() != ElementKind.FIELD
                || ElementUtils.isStatic(elt)
                || type.isAnnotatedInHierarchy(DET)) {
            super.applyQualifierParameterDefaults(elt, type);
            return;
        }

        TypeElement enclosingClass = ElementUtils.enclosingClass(elt);
        Set<AnnotationMirror> tops = getQualifierParameterHierarchies(enclosingClass);
        if (AnnotationUtils.containsSameByClass(tops, NonDet.class)) {
            type.addAnnotation(POLYDET);
        }
        super.applyQualifierParameterDefaults(elt, type);
    }
}
