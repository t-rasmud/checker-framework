//package org.checkerframework.checker.iteration;
//
//import org.checkerframework.checker.iteration.qual.EnsuresNonEmpty;
//import org.checkerframework.checker.iteration.qual.EnsuresNonEmptyIf;
//import org.checkerframework.checker.iteration.qual.NonEmpty;
//import org.checkerframework.checker.iteration.qual.NonEmptyBottom;
//import org.checkerframework.checker.iteration.qual.PolyNonEmpty;
//import org.checkerframework.checker.iteration.qual.UnknownNonEmpty;
//import org.checkerframework.checker.nullness.qual.Nullable;
//import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
//import org.checkerframework.common.basetype.BaseTypeChecker;
//import org.checkerframework.framework.type.ElementQualifierHierarchy;
//import org.checkerframework.framework.type.QualifierHierarchy;
//
//import javax.lang.model.element.AnnotationMirror;
//import javax.lang.model.util.Elements;
//import java.lang.annotation.Annotation;
//import java.util.Arrays;
//import java.util.LinkedHashSet;
//import java.util.Set;
//import org.checkerframework.javacutil.AnnotationBuilder;
//
//
//public class NonEmptyAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
//
//    public final AnnotationMirror UNKNOWNNONEMPTY =
//        AnnotationBuilder.fromClass(elements, UnknownNonEmpty.class);
//    public final AnnotationMirror NONEMPTYBOTTOM =
//        AnnotationBuilder.fromClass(elements, NonEmptyBottom.class);
//
//    public NonEmptyAnnotatedTypeFactory(BaseTypeChecker checker) {
//        super(checker);
//        this.postInit();
//    }
//    @Override
//    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
//        return new LinkedHashSet<>(
//            Arrays.asList(UnknownNonEmpty.class, NonEmpty.class,
//                NonEmptyBottom.class, PolyNonEmpty.class,
//                EnsuresNonEmpty.class, EnsuresNonEmptyIf.class));
//    }
//
//    @Override
//    protected QualifierHierarchy createQualifierHierarchy() {
//        return new NonEmptyQualifierHierarchy(this.getSupportedTypeQualifiers(), elements);
//    }
//
//    private final class NonEmptyQualifierHierarchy extends ElementQualifierHierarchy {
//
//        public NonEmptyQualifierHierarchy(
//            Set<Class<? extends Annotation>> qualifierClasses, Elements elements) {
//            super(qualifierClasses, elements);
//        }
//
//        @Override
//        public boolean isSubtype(AnnotationMirror subQualifier, AnnotationMirror superQualifier) {
//            if (areSameByClass(subQualifier, NonEmptyBottom.class)) {
//                return true;
//            } else if (areSameByClass(superQualifier, UnknownNonEmpty.class)) {
//                return true;
//            } else if (areSameByClass(subQualifier, PolyNonEmpty.class)) {
//                return areSameByClass(superQualifier, PolyNonEmpty.class);
//            }
//            return false;
//        }
//
//        @Override
//        public @Nullable AnnotationMirror leastUpperBound(AnnotationMirror qualifier1, AnnotationMirror qualifier2) {
//            if (areSameByClass(qualifier1, NonEmptyBottom.class)) {
//                return qualifier2;
//            } else if (areSameByClass(qualifier2, NonEmptyBottom.class)) {
//                return qualifier1;
//            } else if (areSameByClass(qualifier1, PolyNonEmpty.class)
//                && areSameByClass(qualifier2, PolyNonEmpty.class)) {
//                return qualifier1;
//            } else {
//                return UNKNOWNNONEMPTY;
//            }
//        }
//
//        @Override
//        public @Nullable AnnotationMirror greatestLowerBound(AnnotationMirror qualifier1, AnnotationMirror qualifier2) {
//            if (areSameByClass(qualifier1, UnknownNonEmpty.class)) {
//                return qualifier2;
//            } else if (areSameByClass(qualifier2, UnknownNonEmpty.class)) {
//                return qualifier1;
//            } else {
//                return NONEMPTYBOTTOM;
//            }
//        }
//    }
//}
