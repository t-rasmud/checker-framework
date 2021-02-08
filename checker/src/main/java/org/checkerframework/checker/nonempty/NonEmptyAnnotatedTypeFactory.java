// package org.checkerframework.checker.nonempty;
//
// import com.sun.source.tree.LiteralTree;
// import com.sun.source.tree.MethodInvocationTree;
// import com.sun.source.tree.Tree;
// import javax.lang.model.element.AnnotationMirror;
// import javax.lang.model.element.ExecutableElement;
// import org.checkerframework.checker.nonempty.qual.NonEmpty;
//// import org.checkerframework.checker.nonempty.qual.PositiveInt;
// import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
// import org.checkerframework.common.basetype.BaseTypeChecker;
// import org.checkerframework.framework.type.AnnotatedTypeMirror;
// import org.checkerframework.framework.type.treeannotator.ListTreeAnnotator;
// import org.checkerframework.framework.type.treeannotator.TreeAnnotator;
// import org.checkerframework.javacutil.AnnotationBuilder;
// import org.checkerframework.javacutil.TreeUtils;
//
// public class NonEmptyAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
//    /** The @{@link PositiveInt} annotation. */
//    protected final AnnotationMirror POSITIVE_INT =
//            AnnotationBuilder.fromClass(elements, PositiveInt.class);
//    /** The {@code Collection.size()} method */
//    ExecutableElement sizeMethod =
//            TreeUtils.getMethod("java.util.Collection", "size", 0, processingEnv);
//
//    public NonEmptyAnnotatedTypeFactory(BaseTypeChecker checker) {
//        super(checker);
//        this.postInit();
//    }
//
//    @Override
//    protected TreeAnnotator createTreeAnnotator() {
//        return new ListTreeAnnotator(super.createTreeAnnotator(), new
// NonEmptyTreeAnnotator(this));
//    }
//
//    private class NonEmptyTreeAnnotator extends TreeAnnotator {
//
//        protected NonEmptyTreeAnnotator(NonEmptyAnnotatedTypeFactory atypeFactory) {
//            super(atypeFactory);
//        }
//
//        @Override
//        public Void visitLiteral(LiteralTree node, AnnotatedTypeMirror annotatedTypeMirror) {
//            Void result = super.visitLiteral(node, annotatedTypeMirror);
//            if (node.getKind() == Tree.Kind.INT_LITERAL) {
//                Integer value = (Integer) node.getValue();
//                if (value > 0) {
//                    annotatedTypeMirror.replaceAnnotation(POSITIVE_INT);
//                }
//            }
//            return result;
//        }
//
//        @Override
//        public Void visitMethodInvocation(
//                MethodInvocationTree node, AnnotatedTypeMirror annotatedTypeMirror) {
//            Void result = super.visitMethodInvocation(node, annotatedTypeMirror);
//            if (TreeUtils.isMethodInvocation(node, sizeMethod, processingEnv)) {
//                AnnotatedTypeMirror receiverType = getReceiverType(node);
//                if (receiverType.hasAnnotation(NonEmpty.class)) {
//                    annotatedTypeMirror.replaceAnnotation(POSITIVE_INT);
//                }
//            }
//            return result;
//        }
//    }
// }
