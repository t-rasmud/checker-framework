package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for bitwise right shift operations with sign extension:
 *
 * <pre>
 *   <em>expression</em> &gt;&gt; <em>expression</em>
 * </pre>
 */
public class SignedRightShiftNode extends BinaryOperationNode {

    public SignedRightShiftNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.RIGHT_SHIFT;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitSignedRightShift(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet SignedRightShiftNode this) {
        return "(" + getLeftOperand() + " >> " + getRightOperand() + ")";
    }

    @Override
    @SuppressWarnings("determinism") // calling equals on two @PolyDet returns @NonDet
    public @PolyDet boolean equals(
            @PolyDet SignedRightShiftNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof SignedRightShiftNode)) {
            return false;
        }
        SignedRightShiftNode other = (SignedRightShiftNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet SignedRightShiftNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
