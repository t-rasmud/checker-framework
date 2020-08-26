package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the floating-point remainder:
 *
 * <pre>
 *   <em>expression</em> % <em>expression</em>
 * </pre>
 */
public class FloatingRemainderNode extends BinaryOperationNode {

    public FloatingRemainderNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.REMAINDER;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitFloatingRemainder(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet FloatingRemainderNode this) {
        return "(" + getLeftOperand() + " % " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet FloatingRemainderNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof FloatingRemainderNode)) {
            return false;
        }
        FloatingRemainderNode other = (FloatingRemainderNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet FloatingRemainderNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
