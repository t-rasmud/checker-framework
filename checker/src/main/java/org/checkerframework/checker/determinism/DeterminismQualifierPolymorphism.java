package org.checkerframework.checker.determinism;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.poly.DefaultQualifierPolymorphism;
import org.checkerframework.framework.type.visitor.AnnotatedTypeScanner;
import org.checkerframework.framework.util.AnnotationMirrorMap;
import org.checkerframework.framework.util.AnnotationMirrorSet;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.BugInCF;
import org.checkerframework.javacutil.TreeUtils;

/**
 * Resolves polymorphic annotations at method invocations as follows:
 *
 * <ol>
 *   <li>Resolves a type annotated as {@code @PolyDet("up")} to {@code @NonDet} if {@code @PolyDet}
 *       resolves to {@code OrderNonDet}.
 *   <li>Resolves a type annotated as {@code @PolyDet("down")} to {@code @Det} if {@code @PolyDet}
 *       resolves to {@code OrderNonDet}.
 *   <li>Resolves a type annotated as {@code @PolyDet("use")} to the same annotation that
 *       {@code @PolyDet} resolves to for the other arguments.
 * </ol>
 */
public class DeterminismQualifierPolymorphism extends DefaultQualifierPolymorphism {

    /** Determinism Checker type factory. */
    DeterminismAnnotatedTypeFactory factory;

    /**
     * Creates a {@link DefaultQualifierPolymorphism} instance that uses the Determinism Checker for
     * querying type qualifiers and the {@link DeterminismAnnotatedTypeFactory} for getting
     * annotated types.
     *
     * @param env the processing environment
     * @param factory the type factory for the Determinism Checker
     */
    public DeterminismQualifierPolymorphism(
            ProcessingEnvironment env, DeterminismAnnotatedTypeFactory factory) {
        super(env, factory);
        this.factory = factory;
    }

    /**
     * NOTE: {@code @PolyDet("noOrderNonDet")} is a polymorphic type that can be instantiated as
     * {@code @NonDet} or {@code @Det}, but not {@code OrderNonDet}.
     *
     * <p>Treats {@code @PolyDet("noOrderNonDet")} written on a formal parameter as {@code @Det} if
     * the corresponding argument is of type type {@code @OrderNonDet}, {@code @PolyDet}, or
     * {@code @PolyDet("upDet")}. Treats {@code @PolyDet("noOrderNonDet")} as {@code @PolyDet}
     * otherwise.
     */
    @Override
    public void resolve(
            MethodInvocationTree tree, AnnotatedTypeMirror.AnnotatedExecutableType type) {
        List<AnnotatedTypeMirror> paramTypes = type.getParameterTypes();
        List<? extends ExpressionTree> argTypes = tree.getArguments();

        AnnotatedDeclaredType paramReceiver = type.getReceiverType();
        if (paramReceiver != null) {
            AnnotationMirror paramReceiverAnno =
                    paramReceiver.getAnnotationInHierarchy(factory.NONDET);
            ExpressionTree argReceiverTree = TreeUtils.getReceiverTree(tree);
            if (argReceiverTree != null) {
                AnnotationMirror argReceiverAnno =
                        factory.getAnnotatedType(argReceiverTree)
                                .getAnnotationInHierarchy(factory.NONDET);

                if (AnnotationUtils.areSame(paramReceiverAnno, factory.POLYDET_NOORDERNONDET)) {
                    if (AnnotationUtils.areSame(argReceiverAnno, factory.ORDERNONDET)
                            || AnnotationUtils.areSame(argReceiverAnno, factory.POLYDET)
                            || AnnotationUtils.areSame(argReceiverAnno, factory.POLYDET_UPDET)) {
                        paramReceiver.replaceAnnotation(factory.DET);
                    } else {
                        paramReceiver.replaceAnnotation(factory.POLYDET);
                    }
                }
            }
        }

        for (AnnotatedTypeMirror param : paramTypes) {
            AnnotationMirror paramAnno = param.getAnnotationInHierarchy(factory.NONDET);
            if (AnnotationUtils.areSame(paramAnno, factory.POLYDET_NOORDERNONDET)) {
                int paramIndex = paramTypes.indexOf(param);
                ExpressionTree argNoOrderNonDet = argTypes.get(paramIndex);
                AnnotationMirror argAnno =
                        factory.getAnnotatedType(argNoOrderNonDet)
                                .getAnnotationInHierarchy(factory.NONDET);
                if (AnnotationUtils.areSame(argAnno, factory.ORDERNONDET)
                        || AnnotationUtils.areSame(argAnno, factory.POLYDET)
                        || AnnotationUtils.areSame(argAnno, factory.POLYDET_UPDET)) {
                    param.replaceAnnotation(factory.DET);
                } else {
                    param.replaceAnnotation(factory.POLYDET);
                }
            }
        }
        super.resolve(tree, type);
    }

    /**
     * Replaces {@code @PolyDet} in {@code type} with the instantiations in {@code
     * replacementsMapping}. Replaces {@code @PolyDet("up")} with {@code @NonDet} if it resolves to
     * {@code OrderNonDet}. Replaces {@code @PolyDet("down")} with {@code @Det} if it resolves to
     * {@code OrderNonDet}. Replaces {@code @PolyDet("use")} with the same annotation that
     * {@code @PolyDet} resolves to. Replaces {@code @PolyDet("upDet")} with {@code @OrderNonDet} if
     * it resolves to {@code @Det}.
     *
     * @param type annotated type whose poly annotations are replaced
     * @param replacementsMapping mapping from polymorphic annotation to instantiation
     */
    @Override
    protected void replace(
            AnnotatedTypeMirror type,
            AnnotationMirrorMap<AnnotationMirrorSet> replacementsMapping) {
        AnnotationMirror anno = type.getAnnotation(PolyDet.class);
        if (anno == null) {
            return;
        }
        String value = AnnotationUtils.getElementValue(anno, "value", String.class, true);
        AnnotationMirrorSet replacements = replacementsMapping.get(factory.POLYDET);
        switch (value) {
            case "":
                type.replaceAnnotations(replacements);
                return;
            case "use":
                if (!replacements.contains(factory.POLYDET)
                        && !replacements.contains(factory.POLYDET_UP)
                        && !replacements.contains(factory.POLYDET_DOWN)
                        && !replacements.contains(factory.POLYDET_UPDET)) {
                    type.replaceAnnotations(replacements);
                }
                return;
            case "up":
                if (replacements.contains(factory.DET)) {
                    type.replaceAnnotations(replacements);
                } else if (replacements.contains(factory.ORDERNONDET)
                        || replacements.contains(factory.NONDET)) {
                    replaceForPolyWithModifier(type, factory.NONDET);
                }
                return;
            case "down":
                if (replacements.contains(factory.NONDET)) {
                    type.replaceAnnotations(replacements);
                } else if (replacements.contains(factory.ORDERNONDET)
                        || replacements.contains(factory.DET)) {
                    replaceForPolyWithModifier(type, factory.DET);
                }
                return;
            case "upDet":
                if (replacements.contains(factory.NONDET)
                        || replacements.contains(factory.ORDERNONDET)) {
                    type.replaceAnnotations(replacements);
                } else if (replacements.contains(factory.DET)) {
                    replaceForPolyWithModifier(type, factory.ORDERNONDET);
                }
                return;
            default:
                throw new BugInCF("Unexpected value in @PolyDet: " + value);
        }
    }

    /**
     * This method replaces the annotation of {@code type} and all its nested type arguments (if
     * {@code type} is a Collection or Iterator) or component types (if {@code type} is an array)
     * with {@code replaceType}.
     *
     * @param type the polymorphic type to be replaced
     * @param replaceType the type to be replaced with
     */
    private void replaceForPolyWithModifier(
            AnnotatedTypeMirror type, AnnotationMirror replaceType) {
        type.replaceAnnotation(replaceType);
        if (!(factory.isCollection(type)
                || factory.isMap(type)
                || factory.isIterator(type)
                || type.getKind() == TypeKind.ARRAY)) {
            return;
        }
        new CollectionReplacer().visit(type, replaceType);
    }

    /**
     * Replaces the annotation on {@code type} with {@code replaceType}. If {@code type} is a
     * Collection (or a subtype) or an Iterator (or a subtype), iterates over all the nested
     * Collection/Iterator type arguments of {@code type} and replaces their top-level annotations
     * with {@code replaceType}. If {@code type} is an array, iterates over all nested component
     * types of {@code type} and replaces their top level annotations with {@code replaceType}.
     *
     * <p>Example1: If this method is called with {@code type} as {@code @OrderNonDet Integer} and
     * {@code replaceType} as {@code @NonDet}, the resulting {@code type} will be {@code @NonDet
     * Integer}.
     *
     * <p>Example2: If this method is called with {@code type} as {@code @OrderNonDet Set<@Det
     * Integer>} and {@code replaceType} as {@code @NonDet}, the resulting {@code type} will be
     * {@code @NonDet Set<@Det Integer>}.
     *
     * <p>Example3: If this method is called with {@code type} as {@code @OrderNonDet
     * Set<@OrderNonDet Set<@Det Integer>>} and {@code replaceType} as {@code @NonDet}, the result
     * will be {@code @NonDet Set<@NonDet Set<@Det Integer>>}.
     *
     * <p>Example4: If this method is called with {@code type} as {@code @OrderNonDet
     * Set<@OrderNonDet Set<@OrderNonDet List<@Det Integer>>>} and {@code replaceType} as
     * {@code @Det}, the result will be {@code @Det Set<@Det Set<@Det List<@Det Integer>>>}.
     *
     * <p>Example5: If this method is called with {@code type} as {@code @Det
     * int @OrderNonDet[] @OrderNonDet} and {@code replaceType} as {@code @NonDet}, the result will
     * be {@code @Det int @NonDet[] @NonDet}.
     */
    class CollectionReplacer extends AnnotatedTypeScanner<Void, AnnotationMirror> {
        @Override
        public Void visitDeclared(AnnotatedDeclaredType type, AnnotationMirror annotationMirror) {
            if (!(factory.isCollection(type)
                    || factory.isMap(type)
                    || factory.isIterator(type)
                    || type.getKind() == TypeKind.ARRAY)) {
                // Don't look further.
                return null;
            }
            if (!type.getTypeArguments().isEmpty()) {
                type.replaceAnnotation(annotationMirror);
            }
            return super.visitDeclared(type, annotationMirror);
        }

        @Override
        public Void visitArray(AnnotatedArrayType type, AnnotationMirror annotationMirror) {
            type.replaceAnnotation(annotationMirror);
            return super.visitArray(type, annotationMirror);
        }
    }
}
