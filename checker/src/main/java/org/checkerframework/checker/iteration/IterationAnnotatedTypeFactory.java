package org.checkerframework.checker.iteration;

import com.sun.source.tree.ExpressionTree;
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

/** Annotated type factory for the Iteration Checker. */
public class IterationAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    /** {@link HasNext}. */
    private final AnnotationMirror HASNEXT = AnnotationBuilder.fromClass(elements, HasNext.class);

    /**
     * Constructor for IterationAnnotatedTypeFactory.
     *
     * @param checker BaseTypeChecker
     */
    public IterationAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    public TreeAnnotator createTreeAnnotator() {
        return new ListTreeAnnotator(super.createTreeAnnotator(), new IterationTreeAnnotator(this));
    }

    /** Tree Annotator for the Iteration Checker. */
    protected class IterationTreeAnnotator extends TreeAnnotator {

        /**
         * Tree annotator.
         *
         * @param atypeFactory AnnotatedTypeFactory
         */
        protected IterationTreeAnnotator(AnnotatedTypeFactory atypeFactory) {
            super(atypeFactory);
        }

        /**
         * Annotates the return type of Collection.iterator() as {@code @HasNext} if the Collection
         * is annotated as {@code @NonEmpty} in the NonEmpty Checker.
         *
         * <p>Annotates the return type of ImageIO.getImageWritersByFormatName() as {@code HasNext}
         * if the argument to ImageIO.getImageWritersByFormatName() is jpeg.
         *
         * @param node MethodInvocationTree
         * @param annotatedTypeMirror AnnotatedTypeMirror
         * @return Void
         */
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

            ExecutableElement getImageWritersMethod =
                    TreeUtils.getMethod(
                            "javax.imageio.ImageIO",
                            "getImageWritersByFormatName",
                            1,
                            processingEnv);
            if (TreeUtils.isMethodInvocation(node, getImageWritersMethod, processingEnv)) {
                ExpressionTree argument = node.getArguments().get(0);
                if (argument.toString().equals("\"jpeg\"")) {
                    annotatedTypeMirror.replaceAnnotation(HASNEXT);
                }
            }

            return super.visitMethodInvocation(node, annotatedTypeMirror);
        }
    }
}
