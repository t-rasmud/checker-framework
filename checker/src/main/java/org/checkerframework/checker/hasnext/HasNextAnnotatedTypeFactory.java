package org.checkerframework.checker.hasnext;

import com.sun.source.tree.MethodInvocationTree;
import java.lang.annotation.Annotation;
import java.util.Set;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.hasnext.qual.HasNextTrue;
import org.checkerframework.checker.hasnext.qual.UnknownHasNext;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.*;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.ElementQualifierHierarchy;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;

public class HasNextAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    public final AnnotationMirror UNKNOWNHASNEXT =
            AnnotationBuilder.fromClass(elements, UnknownHasNext.class);
    public final AnnotationMirror HASNEXTTRUE =
            AnnotationBuilder.fromClass(elements, HasNextTrue.class);

    public HasNextAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    public TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(new HasNextTreeAnnotator(this), super.createTreeAnnotator());
    }

    private class HasNextTreeAnnotator extends TreeAnnotator {
        private final ProcessingEnvironment processingEnv = getProcessingEnv();

        public HasNextTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        public Void visitMethodInvocation(
                MethodInvocationTree node, AnnotatedTypeMirror annotatedTypeMirror) {
            AnnotatedTypeMirror receiverType = getReceiverType(node);
            ExecutableElement iteratorNext =
                    TreeUtils.getMethod("java.util.Iterator", "next", 0, processingEnv);
            if (TreeUtils.isMethodInvocation(node, iteratorNext, processingEnv)) {
                if (!receiverType.hasAnnotation(HASNEXTTRUE)) {
                    checker.reportError(node, "illegal.next");
                }
            }
            return super.visitMethodInvocation(node, annotatedTypeMirror);
        }
    }

    @Override
    public CFTransfer createFlowTransferFunction(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        return new HasNextTransfer((CFAnalysis) analysis);
    }

    @Override
    protected QualifierHierarchy createQualifierHierarchy() {
        return new HasNextQualifierHierarchy(this.getSupportedTypeQualifiers(), elements);
    }

    private final class HasNextQualifierHierarchy extends ElementQualifierHierarchy {
        public HasNextQualifierHierarchy(
                Set<Class<? extends Annotation>> qualifierClasses, Elements elements) {
            super(qualifierClasses, elements);
        }

        @Override
        public boolean isSubtype(AnnotationMirror subAnno, AnnotationMirror superAnno) {
            if (areSameByClass(superAnno, UnknownHasNext.class)) {
                return true;
            } else if (areSameByClass(superAnno, UnknownHasNext.class)
                    && areSameByClass(subAnno, HasNextTrue.class)) {
                return true;
            }
            return false;
        }

        @Override
        public @Nullable AnnotationMirror leastUpperBound(
                AnnotationMirror annotationMirror, AnnotationMirror annotationMirror1) {
            if (isSubtype(annotationMirror, annotationMirror1)) {
                return annotationMirror1;
            } else if (isSubtype(annotationMirror1, annotationMirror)) {
                return annotationMirror;
            }
            return UNKNOWNHASNEXT;
        }

        @Override
        public @Nullable AnnotationMirror greatestLowerBound(
                AnnotationMirror annotationMirror, AnnotationMirror annotationMirror1) {
            if (isSubtype(annotationMirror, annotationMirror1)) {
                return annotationMirror;
            } else if (isSubtype(annotationMirror1, annotationMirror)) {
                return annotationMirror1;
            }
            return HASNEXTTRUE;
        }
    }
}
