package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the numerical addition:
 *
 * <pre>
 *   <em>expression</em> + <em>expression</em>
 * </pre>
 */
public class NumericalAdditionNode extends BinaryOperationNode {

    public NumericalAdditionNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.PLUS || tree.getKind() == Kind.PLUS_ASSIGNMENT;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitNumericalAddition(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet NumericalAdditionNode this) {
        return "(" + getLeftOperand() + " + " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet NumericalAdditionNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof NumericalAdditionNode)) {
            return false;
        }
        NumericalAdditionNode other = (NumericalAdditionNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet NumericalAdditionNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
