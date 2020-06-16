package org.checkerframework.checker.determinism;

import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.poly.DefaultQualifierPolymorphism;
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
        this.polyQuals.put(factory.POLYDET_USE, factory.NONDET);
        this.polyQuals.put(factory.POLYDET_USENOORDERNONDET, factory.NONDET);
    }

    /**
     * Replaces {@code @PolyDet} in {@code type} with the instantiations in {@code
     * replacementsMapping}. Replaces {@code @PolyDet("up")} with {@code @NonDet} if it resolves to
     * {@code OrderNonDet}. Replaces {@code @PolyDet("down")} with {@code @Det} if it resolves to
     * {@code OrderNonDet}. Replaces {@code @PolyDet("use")} with the same annotation that
     * {@code @PolyDet} resolves to if {@code @PolyDet("use")} resolves to an annotation that is a
     * supertype of the annotation that {@code @PolyDet} resolves to. Replaces
     * {@code @PolyDet("upDet")} with {@code @OrderNonDet} if it resolves to {@code @Det}. Replaces
     * {@code @PolyDet("noOrderNonDet")} with {@code @Det} if it resolves to {@code OrderNonDet}.
     * Replaces {@code @PolyDet("useNoOrderNonDet")} with the same annotation that
     * {@code @PolyDet("noOrderNonDet")} resolves to if {@code @PolyDet("useNoOrderNonDet")}
     * resolves to an annotation that is a supertype of the annotation that
     * {@code @PolyDet("noOrderNonDet")} resolves to.
     *
     * @checker_framework.manual #determinism-polymorphism Determinism Polymorphism
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
        if (replacements == null) {
            return;
        }

        switch (value) {
            case "":
                type.replaceAnnotations(replacements);
                return;
            case "up":
                if (replacements.contains(factory.DET)) {
                    type.replaceAnnotations(replacements);
                } else if (replacements.contains(factory.ORDERNONDET)
                        || replacements.contains(factory.NONDET)
                        || replacements.contains(factory.POLYDET_UPDET)) {
                    type.replaceAnnotation(factory.NONDET);
                }
                return;
            case "down":
                if (replacements.contains(factory.NONDET)) {
                    type.replaceAnnotations(replacements);
                } else if (replacements.contains(factory.ORDERNONDET)
                        || replacements.contains(factory.DET)) {
                    type.replaceAnnotation(factory.DET);
                }
                return;
            case "upDet":
                if (replacements.contains(factory.NONDET)
                        || replacements.contains(factory.ORDERNONDET)) {
                    type.replaceAnnotations(replacements);
                } else if (replacements.contains(factory.DET)) {
                    type.replaceAnnotation(factory.ORDERNONDET);
                }
                return;
            case "noOrderNonDet":
                if (replacements.contains(factory.ORDERNONDET)
                        || replacements.contains(factory.POLYDET)) {
                    type.replaceAnnotation(factory.DET);
                } else {
                    type.replaceAnnotations(replacements);
                }
                return;
            case "use":
                if (replacementsPolyDet == null) {
                    return;
                }
                // Replace @PolyDet("use") with @PolyDet if @PolyDet("use") doesn't
                // resolve to a type that is a subtype of what @PolyDet resolves to.
                AnnotationMirrorSet replacementsPolyDetUse =
                        replacementsMapping.get(factory.POLYDET_USE);
                if (replacementsPolyDetUse == null) {
                    type.replaceAnnotations(replacementsPolyDet);
                    return;
                }
                for (AnnotationMirror poly : replacementsPolyDet) {
                    for (AnnotationMirror polyUse : replacementsPolyDetUse) {
                        if (!atypeFactory.getQualifierHierarchy().isSubtype(polyUse, poly)) {
                            type.replaceAnnotations(replacementsPolyDet);
                            return;
                        }
                    }
                }
                type.replaceAnnotations(replacementsPolyDetUse);
                return;
            case "useNoOrderNonDet":
                if (replacementsPolyDetNoOND == null) {
                    return;
                }
                // Replace @PolyDet("useNoOrderNonDet") with @PolyDet("noOrderNonDet")
                // if @PolyDet("useNoOrderNonDet") doesn't resolve to a type that is a subtype of
                // what @PolyDet("noOrderNonDet") resolves to.
                AnnotationMirrorSet replacementsPolyDetUseNoOrderNonDet =
                        replacementsMapping.get(factory.POLYDET_USENOORDERNONDET);
                if (replacementsPolyDetUseNoOrderNonDet == null) {
                    type.replaceAnnotations(replacementsPolyDetNoOND);
                    return;
                }
                for (AnnotationMirror poly : replacementsPolyDetNoOND) {
                    for (AnnotationMirror polyUse : replacementsPolyDetUseNoOrderNonDet) {
                        if (!atypeFactory.getQualifierHierarchy().isSubtype(polyUse, poly)) {
                            type.replaceAnnotations(replacementsPolyDetNoOND);
                            return;
                        }
                    }
                }
                type.replaceAnnotations(replacementsPolyDetUseNoOrderNonDet);

                return;
            default:
                throw new BugInCF("Unexpected value in @PolyDet: " + value);
        }
    }
}
