package org.checkerframework.checker.nonempty;

import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.checker.nonempty.qual.NonEmptyBottom;
import org.checkerframework.checker.nonempty.qual.PolyNonEmpty;
import org.checkerframework.checker.nonempty.qual.UnknownNonEmpty;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.ElementQualifierHierarchy;
import org.checkerframework.framework.type.NoElementQualifierHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.checkerframework.javacutil.AnnotationBuilder;

/**
 * Defines type hierarchy of the NonEmpty Checker qualifiers. This is necessary for
 * Checkers like the Iteration Checker that use the NonEmpty Checker as a subchecker.
 */
public class NonEmptyAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The {@literal @}{@link UnknownNonEmpty} annotation. */
    public final AnnotationMirror UNKNOWNNONEMPTY =
        AnnotationBuilder.fromClass(elements, UnknownNonEmpty.class);
    /** The {@literal @}{@link NonEmpty} annotation. */
    public final AnnotationMirror NONEMPTY =
        AnnotationBuilder.fromClass(elements, NonEmpty.class);
    /** The {@literal @}{@link NonEmptyBottom} annotation. */
    public final AnnotationMirror NONEMPTYBOTTOM =
        AnnotationBuilder.fromClass(elements, NonEmptyBottom.class);

    public NonEmptyAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }
    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return new LinkedHashSet<>(
            Arrays.asList(UnknownNonEmpty.class, NonEmpty.class,
                NonEmptyBottom.class, PolyNonEmpty.class));
    }

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new NonEmptyQualifierHierarchy(this.getSupportedTypeQualifiers(), elements);
    }

    private final class NonEmptyQualifierHierarchy extends NoElementQualifierHierarchy {

        public NonEmptyQualifierHierarchy(
            Set<Class<? extends Annotation>> qualifierClasses, Elements elements) {
            super(qualifierClasses, elements);
        }

        @Override
        public boolean isSubtype(AnnotationMirror subQualifier, AnnotationMirror superQualifier) {
            if (areSameByClass(subQualifier, NonEmptyBottom.class)) {
                return true;
            } else if (areSameByClass(superQualifier, UnknownNonEmpty.class)) {
                return true;
            } else if (areSameByClass(subQualifier, PolyNonEmpty.class)) {
                return areSameByClass(superQualifier, PolyNonEmpty.class);
            } else if (areSameByClass(subQualifier, NonEmpty.class)) {
                return areSameByClass(superQualifier, NonEmpty.class);
            }
            return false;
        }

        @Override
        public @Nullable AnnotationMirror leastUpperBound(AnnotationMirror qualifier1, AnnotationMirror qualifier2) {
            if (areSameByClass(qualifier1, NonEmptyBottom.class)) {
                return qualifier2;
            } else if (areSameByClass(qualifier2, NonEmptyBottom.class)) {
                return qualifier1;
            } else if (areSameByClass(qualifier1, PolyNonEmpty.class)
                && areSameByClass(qualifier2, PolyNonEmpty.class)) {
                return qualifier1;
            } else if (areSameByClass(qualifier1, NonEmpty.class)
                && areSameByClass(qualifier2, NonEmpty.class)) {
                return qualifier1;
            } else {
                return UNKNOWNNONEMPTY;
            }
        }

        @Override
        public @Nullable AnnotationMirror greatestLowerBound(AnnotationMirror qualifier1, AnnotationMirror qualifier2) {
            if (areSameByClass(qualifier1, UnknownNonEmpty.class)) {
                return qualifier2;
            } else if (areSameByClass(qualifier2, UnknownNonEmpty.class)) {
                return qualifier1;
            } else if (areSameByClass(qualifier1, NonEmpty.class)
                && areSameByClass(qualifier2, NonEmpty.class)) {
                return qualifier1;
            } else {
                return NONEMPTYBOTTOM;
            }
        }
    }
}
