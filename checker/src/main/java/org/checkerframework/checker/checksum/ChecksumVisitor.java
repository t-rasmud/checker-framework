package org.checkerframework.checker.checksum;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.UnaryTree;
import java.util.Collections;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.checksum.qual.ChecksummedBy;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;

/** Checksum visitor. */
public class ChecksumVisitor extends BaseTypeVisitor<ChecksumAnnotatedTypeFactory> {
    /**
     * ChecksumVisitor constructor.
     *
     * @param checker BaseTypeChecker
     */
    public ChecksumVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    protected ChecksumAnnotatedTypeFactory createTypeFactory() {
        return new ChecksumAnnotatedTypeFactory(checker);
    }

    /**
     * Reports an error if the return type of a constructor is not annotated with {@code
     * NotChecksummed}.
     */
    @Override
    protected void checkConstructorResult(
            AnnotatedTypeMirror.AnnotatedExecutableType constructorType,
            ExecutableElement constructorElement) {
        if (constructorType.getReturnType().hasAnnotation(atypeFactory.NOT_CHECKSUMMED)) {
            return;
        }
        checker.reportError(constructorElement, "invalid.constructor.return.type");
    }

    /** Makes {@code @NotChecksummed} the default lower bound on Exception parameters. */
    @Override
    protected Set<? extends AnnotationMirror> getExceptionParameterLowerBoundAnnotations() {
        return Collections.singleton(atypeFactory.NOT_CHECKSUMMED);
    }

    /**
     * Reports an error if either operands of a binary operation is annotated with
     * {@code @ChecksummedBy}.
     *
     * @param node BinaryTree
     * @param aVoid Void
     * @return Void
     */
    @Override
    public Void visitBinary(BinaryTree node, Void aVoid) {
        ExpressionTree leftOperand = node.getLeftOperand();
        AnnotatedTypeMirror leftOpAnno = atypeFactory.getAnnotatedType(leftOperand);
        if (leftOpAnno.hasAnnotation(ChecksummedBy.class)) {
            checker.reportError(node, "invalid.operation");
            return null;
        }
        ExpressionTree rightOperand = node.getRightOperand();
        AnnotatedTypeMirror rightOpAnno = atypeFactory.getAnnotatedType(rightOperand);
        if (rightOpAnno.hasAnnotation(ChecksummedBy.class)) {
            checker.reportError(node, "invalid.operation");
            return null;
        }
        return super.visitBinary(node, aVoid);
    }

    /**
     * Reports an error if the unary operand is annotated wieh {@code @ChecksummedBy}.
     *
     * @param node UnaryTree
     * @param p Void
     * @return Void
     */
    @Override
    public Void visitUnary(UnaryTree node, Void p) {
        ExpressionTree operand = node.getExpression();
        AnnotatedTypeMirror operandAnno = atypeFactory.getAnnotatedType(operand);
        if (operandAnno.hasAnnotation(ChecksummedBy.class)) {
            checker.reportError(node, "invalid.operation");
            return null;
        }
        return super.visitUnary(node, p);
    }
}
