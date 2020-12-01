package org.checkerframework.checker.iteration;

import com.sun.source.tree.MethodInvocationTree;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.iteration.qual.HasNext;
import org.checkerframework.checker.nonempty.NonEmptyChecker;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;

public class IterationAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    private final AnnotationMirror HASNEXT = AnnotationBuilder.fromClass(elements, HasNext.class);

    public IterationAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    public TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(super.createTreeAnnotator(), new IterationTreeAnnotator(this));
    }

    protected class IterationTreeAnnotator extends TreeAnnotator {

        protected IterationTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        @Override
        public Void visitMethodInvocation(
                MethodInvocationTree node, AnnotatedTypeMirror annotatedTypeMirror) {

            ExecutableElement iteratorMethod =
                    TreeUtils.getMethod("java.util.Collection", "iterator", 0, processingEnv);
            if (TreeUtils.isMethodInvocation(node, iteratorMethod, processingEnv)) {
                AnnotatedTypeMirror receiverType =
                        getTypeFactoryOfSubchecker(NonEmptyChecker.class).getReceiverType(node);
                if (receiverType.hasAnnotation(NonEmpty.class)) {
                    annotatedTypeMirror.replaceAnnotation(HASNEXT);
                }
            }

            return super.visitMethodInvocation(node, annotatedTypeMirror);
        }
    }
}
