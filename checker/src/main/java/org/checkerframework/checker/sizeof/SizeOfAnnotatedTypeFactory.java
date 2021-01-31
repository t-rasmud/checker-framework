package org.checkerframework.checker.sizeof;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.sizeof.qual.SizeOfBottom;
import org.checkerframework.checker.sizeof.qual.UnknownSizeOf;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.value.ValueCheckerUtils;
import org.checkerframework.framework.type.ElementQualifierHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class SizeOfAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    /** The @{@link UnknownSizeOf} annotation. */
    public final AnnotationMirror UNKNOWN_SIZEOF =
            AnnotationBuilder.fromClass(elements, UnknownSizeOf.class);
    /** The @{@link SizeOfBottom} annotation. */
    public final AnnotationMirror SIZEOF_BOTTOM =
            AnnotationBuilder.fromClass(elements, SizeOfBottom.class);

    public SizeOfAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new SizeOfQualifierHierarchy(this.getSupportedTypeQualifiers(), elements);
    }

    private class SizeOfQualifierHierarchy extends ElementQualifierHierarchy {

        /**
         * Creates a ElementQualifierHierarchy from the given classes.
         *
         * @param qualifierClasses classes of annotations that are the qualifiers for this hierarchy
         * @param elements element utils
         */
        protected SizeOfQualifierHierarchy(
                Collection<Class<? extends Annotation>> qualifierClasses, Elements elements) {
            super(qualifierClasses, elements);
        }

        @Override
        public boolean isSubtype(AnnotationMirror subQualifier, AnnotationMirror superQualifier) {
            if (areSameByClass(subQualifier, SizeOfBottom.class)) {
                return true;
            }
            if (areSameByClass(superQualifier, UnknownSizeOf.class)) {
                return true;
            }
            if (AnnotationUtils.hasElementValue(subQualifier, "value")
                    && AnnotationUtils.hasElementValue(superQualifier, "value")) {
                List<String> subArrays =
                        ValueCheckerUtils.getValueOfAnnotationWithStringArgument(subQualifier);
                List<String> superArrays =
                        ValueCheckerUtils.getValueOfAnnotationWithStringArgument(superQualifier);

                if (subArrays.containsAll(superArrays)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public @Nullable AnnotationMirror leastUpperBound(
                AnnotationMirror qualifier1, AnnotationMirror qualifier2) {
            if (areSameByClass(qualifier1, SizeOfBottom.class)) {
                return qualifier2;
            }
            if (areSameByClass(qualifier2, SizeOfBottom.class)) {
                return qualifier1;
            }
            if (areSameByClass(qualifier1, UnknownSizeOf.class)) {
                return qualifier1;
            }
            if (areSameByClass(qualifier2, UnknownSizeOf.class)) {
                return qualifier2;
            }
            if (AnnotationUtils.hasElementValue(qualifier1, "value")
                    && AnnotationUtils.hasElementValue(qualifier2, "value")) {
                List<String> subArrays =
                        ValueCheckerUtils.getValueOfAnnotationWithStringArgument(qualifier1);
                List<String> superArrays =
                        ValueCheckerUtils.getValueOfAnnotationWithStringArgument(qualifier2);

                if (subArrays.containsAll(superArrays)) {
                    return qualifier1;
                }
            }
            return UNKNOWN_SIZEOF;
        }

        @Override
        public @Nullable AnnotationMirror greatestLowerBound(
                AnnotationMirror qualifier1, AnnotationMirror qualifier2) {
            if (areSameByClass(qualifier1, SizeOfBottom.class)) {
                return qualifier1;
            }
            if (areSameByClass(qualifier2, SizeOfBottom.class)) {
                return qualifier2;
            }
            if (areSameByClass(qualifier1, UnknownSizeOf.class)) {
                return qualifier2;
            }
            if (areSameByClass(qualifier2, UnknownSizeOf.class)) {
                return qualifier1;
            }
            if (AnnotationUtils.hasElementValue(qualifier1, "value")
                    && AnnotationUtils.hasElementValue(qualifier2, "value")) {
                List<String> subArrays =
                        ValueCheckerUtils.getValueOfAnnotationWithStringArgument(qualifier1);
                List<String> superArrays =
                        ValueCheckerUtils.getValueOfAnnotationWithStringArgument(qualifier2);

                if (subArrays.containsAll(superArrays)) {
                    return qualifier1;
                }
            }
            return SIZEOF_BOTTOM;
        }
    }
}
