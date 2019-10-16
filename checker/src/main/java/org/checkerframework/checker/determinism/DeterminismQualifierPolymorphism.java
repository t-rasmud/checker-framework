package org.checkerframework.checker.determinism;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodInvocationTree;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeKind;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedArrayType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.poly.DefaultQualifierPolymorphism;
import org.checkerframework.framework.type.visitor.AnnotatedTypeScanner;
import org.checkerframework.framework.util.AnnotationMirrorMap;
import org.checkerframework.framework.util.AnnotationMirrorSet;
import org.checkerframework.javacutil.AnnotationUtils;
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
     * Replaces {@code @PolyDet} in {@code type} with the instantiations in {@code replacements}.
     * Replaces {@code @PolyDet("up")} with {@code @NonDet} if it resolves to {@code OrderNonDet}.
     * Replaces {@code @PolyDet("down")} with {@code @Det} if it resolves to {@code OrderNonDet}.
     * Replaces {@code @PolyDet("use")} with the same annotation that {@code @PolyDet} resolves to.
     * Replaces {@code @PolyDet("upDet")} with {@code @OrderNonDet} if it resolves to {@code @Det}.
     *
     * @param type annotated type whose poly annotations are replaced
     * @param replacements mapping from polymorphic annotation to instantiation
     */
    @Override
    protected void replace(
            AnnotatedTypeMirror type, AnnotationMirrorMap<AnnotationMirrorSet> replacements) {
        if (type.hasAnnotation(factory.POLYDET)) {
            AnnotationMirrorSet quals = replacements.get(factory.POLYDET);
            type.replaceAnnotations(quals);
        } else if (type.hasAnnotation(factory.POLYDET_USE)) {
            AnnotationMirrorSet quals = replacements.get(factory.POLYDET);
            if (!quals.contains(factory.POLYDET)
                    && !quals.contains(factory.POLYDET_UP)
                    && !quals.contains(factory.POLYDET_DOWN)
                    && !quals.contains(factory.POLYDET_UPDET)) {
                type.replaceAnnotations(quals);
            }
        } else if (type.hasAnnotation(factory.POLYDET_UP)) {
            AnnotationMirrorSet quals = replacements.get(factory.POLYDET);
            if (quals.contains(factory.DET)) {
                type.replaceAnnotations(quals);
            }
            if (quals.contains(factory.ORDERNONDET)
                    || replacements.get(factory.POLYDET).contains(factory.NONDET)) {
                replaceForPolyWithModifier(type, factory.NONDET);
            }
        } else if (type.hasAnnotation(factory.POLYDET_DOWN)) {
            AnnotationMirrorSet quals = replacements.get(factory.POLYDET);
            if (quals.contains(factory.NONDET)) {
                type.replaceAnnotations(quals);
            }
            if (quals.contains(factory.ORDERNONDET)
                    || replacements.get(factory.POLYDET).contains(factory.DET)) {
                replaceForPolyWithModifier(type, factory.DET);
            }
        } else if (type.hasAnnotation(factory.POLYDET_UPDET)) {
            AnnotationMirrorSet quals = replacements.get(factory.POLYDET);
            if (quals.contains(factory.NONDET) || quals.contains(factory.ORDERNONDET)) {
                type.replaceAnnotations(quals);
            }
            if (quals.contains(factory.DET)) {
                replaceForPolyWithModifier(type, factory.ORDERNONDET);
            }
        } else {
            for (Map.Entry<AnnotationMirror, AnnotationMirrorSet> pqentry :
                    replacements.entrySet()) {
                AnnotationMirror poly = pqentry.getKey();
                if (type.hasAnnotation(poly)) {
                    type.removeAnnotation(poly);
                    AnnotationMirrorSet quals = pqentry.getValue();
                    type.replaceAnnotations(quals);
                }
            }
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
