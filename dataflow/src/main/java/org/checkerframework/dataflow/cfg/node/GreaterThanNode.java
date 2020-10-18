package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the greater than comparison:
 *
 * <pre>
 *   <em>expression</em> &gt; <em>expression</em>
 * </pre>
 */
public class GreaterThanNode extends BinaryOperationNode {

    public GreaterThanNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.GREATER_THAN;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitGreaterThan(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet GreaterThanNode this) {
        return "(" + getLeftOperand() + " > " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet GreaterThanNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof GreaterThanNode)) {
            return false;
        }
        GreaterThanNode other = (GreaterThanNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet GreaterThanNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
