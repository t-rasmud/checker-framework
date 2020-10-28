package org.checkerframework.checker.hasnext;

import com.sun.source.tree.MethodInvocationTree;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.hasnext.qual.HasNextTrue;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;

public class HasNextVisitor extends BaseTypeVisitor<HasNextAnnotatedTypeFactory> {
    ProcessingEnvironment env;
    public final AnnotationMirror HASNEXTTRUE =
            AnnotationBuilder.fromClass(elements, HasNextTrue.class);

    public HasNextVisitor(BaseTypeChecker checker) {
        super(checker);
        env = checker.getProcessingEnvironment();
    }

    @Override
    public Void visitMethodInvocation(MethodInvocationTree node, Void p) {
        AnnotatedTypeMirror receiverType = atypeFactory.getReceiverType(node);
        ExecutableElement iteratorNext = TreeUtils.getMethod("java.util.Iterator", "next", 0, env);
        if (TreeUtils.isMethodInvocation(node, iteratorNext, env)) {
            if (!receiverType.hasAnnotation(HASNEXTTRUE)) {
                checker.reportError(node, "illegal.next");
            }
        }
        return super.visitMethodInvocation(node, p);
    }
}
