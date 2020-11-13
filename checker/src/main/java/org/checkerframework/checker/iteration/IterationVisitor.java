package org.checkerframework.checker.iteration;

import com.sun.source.tree.MethodInvocationTree;
import org.checkerframework.checker.nonempty.NonEmptyChecker;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.TreeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;

public class IterationVisitor extends BaseTypeVisitor<BaseAnnotatedTypeFactory> {
    ProcessingEnvironment processingEnv = atypeFactory.getProcessingEnv();

    public IterationVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    protected void reportMethodInvocabilityError(MethodInvocationTree node, AnnotatedTypeMirror found, AnnotatedTypeMirror expected) {
        ExecutableElement iteratorNext =
            TreeUtils.getMethod("java.util.Iterator", "next", 0, processingEnv);
        if (TreeUtils.isMethodInvocation(node, iteratorNext, processingEnv)) {
            AnnotatedTypeMirror receiverTypeNonEmpty =
                atypeFactory.getTypeFactoryOfSubchecker(NonEmptyChecker.class).getReceiverType(node);
            if (receiverTypeNonEmpty.hasAnnotation(NonEmpty.class)) {
                return;
            }
        }
        super.reportMethodInvocabilityError(node, found, expected);
    }
}
