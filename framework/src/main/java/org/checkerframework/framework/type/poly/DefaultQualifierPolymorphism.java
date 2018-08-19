package org.checkerframework.framework.type.poly;

import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.Tree;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Name;
import javax.lang.model.util.Elements;
import org.checkerframework.framework.qual.PolymorphicQualifier;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.util.AnnotationMirrorMap;
import org.checkerframework.framework.util.AnnotationMirrorSet;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;
import org.checkerframework.javacutil.ErrorReporter;

/**
 * Implements framework support for type qualifier polymorphism. Checkers that wish to use it should
 * add calls to {@link #annotate(MethodInvocationTree, AnnotatedTypeMirror.AnnotatedExecutableType)}
 * to the {@link AnnotatedTypeFactory#addComputedTypeAnnotations(Tree, AnnotatedTypeMirror)} and
 * {@link AnnotatedTypeFactory#addComputedTypeAnnotations(Tree, AnnotatedTypeMirror)} methods.
 *
 * <p>This implementation currently only supports polymorphism for method invocations, for which the
 * return type depends on the unification of the parameter/receiver types.
 *
 * @see PolymorphicQualifier
 */
public class DefaultQualifierPolymorphism extends AbstractQualifierPolymorphism {

    /**
     * Creates a {@link DefaultQualifierPolymorphism} instance that uses the given checker for
     * querying type qualifiers and the given factory for getting annotated types.
     *
     * @param env the processing environment
     * @param factory the factory for the current checker
     */
    public DefaultQualifierPolymorphism(ProcessingEnvironment env, AnnotatedTypeFactory factory) {
        super(env, factory);
        Elements elements = env.getElementUtils();
        AnnotationMirrorMap<AnnotationMirror> polys = new AnnotationMirrorMap<>();
        for (AnnotationMirror aam : qualhierarchy.getTypeQualifiers()) {
            if (QualifierPolymorphism.isPolyAll(aam)) {
                polys.put(aam, null);
                continue;
            }
            AnnotationMirrorSet topsSeen = new AnnotationMirrorSet();
            for (AnnotationMirror aa : aam.getAnnotationType().asElement().getAnnotationMirrors()) {
                if (aa.getAnnotationType()
                        .toString()
                        .equals(PolymorphicQualifier.class.getCanonicalName())) {
                    Name plval = AnnotationUtils.getElementValueClassName(aa, "value", true);
                    AnnotationMirror ttreetop;
                    if (PolymorphicQualifier.class.getCanonicalName().contentEquals(plval)) {
                        if (topQuals.size() != 1) {
                            ErrorReporter.errorAbort(
                                    "DefaultQualifierPolymorphism: PolymorphicQualifier has to specify type hierarchy, if more than one exist; top types: "
                                            + topQuals);
                        }
                        ttreetop = topQuals.iterator().next();
                    } else {
                        AnnotationMirror ttree = AnnotationBuilder.fromName(elements, plval);
                        ttreetop = qualhierarchy.getTopAnnotation(ttree);
                    }
                    if (topsSeen.contains(ttreetop)) {
                        ErrorReporter.errorAbort(
                                "DefaultQualifierPolymorphism: checker has multiple polymorphic qualifiers: "
                                        + polys.get(ttreetop)
                                        + " and "
                                        + aam);
                    }
                    topsSeen.add(ttreetop);
                    polys.put(aam, ttreetop);
                }
            }
        }

        this.polyQuals.putAll(polys);
    }

    @Override
    protected void replace(
            AnnotatedTypeMirror type, AnnotationMirrorMap<AnnotationMirrorSet> replacements) {
        for (Map.Entry<AnnotationMirror, AnnotationMirrorSet> pqentry : replacements.entrySet()) {
            AnnotationMirror poly = pqentry.getKey();
            if (poly != null && type.hasAnnotation(poly)) {
                type.removeAnnotation(poly);
                AnnotationMirrorSet quals = pqentry.getValue();
                type.replaceAnnotations(quals);
            }
        }
    }

    /**
     * Returns the lub of the two sets.
     *
     * @param polyQual polymorphic qualifier for which {@code a1Annos} and {@code a2Annos} are
     *     instantiations
     * @param a1Annos a set that is an instantiation of {@code polyQual}
     * @param a2Annos a set that is an instantiation of {@code polyQual}
     * @return the lub of the two sets
     */
    @Override
    protected AnnotationMirrorSet combine(
            AnnotationMirror polyQual, AnnotationMirrorSet a1Annos, AnnotationMirrorSet a2Annos) {
        if (a1Annos == null) {
            if (a2Annos == null) {
                return new AnnotationMirrorSet();
            }
            return a2Annos;
        } else if (a2Annos == null) {
            return a1Annos;
        }

        AnnotationMirrorSet lubSet = new AnnotationMirrorSet();
        for (AnnotationMirror top : topQuals) {
            AnnotationMirror a1 = qualhierarchy.findAnnotationInHierarchy(a1Annos, top);
            AnnotationMirror a2 = qualhierarchy.findAnnotationInHierarchy(a2Annos, top);
            AnnotationMirror lub = qualhierarchy.leastUpperBoundTypeVariable(a1, a2);
            if (lub != null) {
                lubSet.add(lub);
            }
        }
        return lubSet;
    }
}
