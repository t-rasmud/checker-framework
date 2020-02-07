package org.checkerframework.checker.determinism;

import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
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
        this.polyQuals.put(factory.POLYDET_NOORDERNONDET, factory.NONDET);
    }

    /**
     * Replaces {@code @PolyDet} in {@code type} with the instantiations in {@code
     * replacementsMapping}. Replaces {@code @PolyDet("up")} with {@code @NonDet} if it resolves to
     * {@code OrderNonDet}. Replaces {@code @PolyDet("down")} with {@code @Det} if it resolves to
     * {@code OrderNonDet}. Replaces {@code @PolyDet("use")} with the same annotation that
     * {@code @PolyDet} resolves to. Replaces {@code @PolyDet("upDet")} with {@code @OrderNonDet} if
     * it resolves to {@code @Det}. Replaces {@code @PolyDet("noOrderNonDet")} with {@code @Det} if
     * it resolves to {@code OrderNonDet}.
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
        AnnotationMirrorSet replacementsPolyDet = replacementsMapping.get(factory.POLYDET);
        AnnotationMirrorSet replacementsPolyDetNoOND =
                replacementsMapping.get(factory.POLYDET_NOORDERNONDET);
        AnnotationMirrorSet replacements;
        if (replacementsPolyDet == null) {
            replacements = replacementsPolyDetNoOND;
        } else if (replacementsPolyDetNoOND == null) {
            replacements = replacementsPolyDet;
        } else {
            Set<? extends AnnotationMirror> lub =
                    qualHierarchy.leastUpperBounds(replacementsPolyDet, replacementsPolyDetNoOND);
            replacements = new AnnotationMirrorSet(lub);
        }

        switch (value) {
            case "":
                type.replaceAnnotations(replacements);
                return;
            case "use":
                if (replacementsPolyDet != null
                        && !replacementsPolyDet.contains(factory.POLYDET)
                        && !replacementsPolyDet.contains(factory.POLYDET_NOORDERNONDET)
                        && !replacementsPolyDet.contains(factory.POLYDET_UP)
                        && !replacementsPolyDet.contains(factory.POLYDET_DOWN)
                        && !replacementsPolyDet.contains(factory.POLYDET_UPDET)) {
                    type.replaceAnnotations(replacementsPolyDet);
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
            case "noOrderNonDet":
                if (replacements.contains(factory.ORDERNONDET)
                        || replacements.contains(factory.POLYDET)) {
                    replaceForPolyWithModifier(type, factory.DET);
                } else {
                    type.replaceAnnotations(replacements);
                }
                return;
            case "useNoOrderNonDet":
                if (replacementsPolyDetNoOND != null
                        && !replacementsPolyDetNoOND.contains(factory.POLYDET)
                        && !replacementsPolyDetNoOND.contains(factory.POLYDET_NOORDERNONDET)
                        && !replacementsPolyDetNoOND.contains(factory.POLYDET_UP)
                        && !replacementsPolyDetNoOND.contains(factory.POLYDET_DOWN)
                        && !replacementsPolyDetNoOND.contains(factory.POLYDET_UPDET)) {
                    type.replaceAnnotations(replacementsPolyDetNoOND);
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
        if (!factory.isCollectionType(type)) {
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
            if (!factory.isCollectionType(type)) {
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
