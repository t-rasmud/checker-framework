package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the less than comparison:
 *
 * <pre>
 *   <em>expression</em> &lt; <em>expression</em>
 * </pre>
 *
 * We allow less than nodes without corresponding AST {@link Tree}s.
 */
public class LessThanNode extends BinaryOperationNode {

    public LessThanNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.LESS_THAN;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitLessThan(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet LessThanNode this) {
        return "(" + getLeftOperand() + " < " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet LessThanNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof LessThanNode)) {
            return false;
        }
        LessThanNode other = (LessThanNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet LessThanNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
