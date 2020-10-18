package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.UnaryTree;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for a conditional not expression:
 *
 * <pre>
 *   ! <em>expression</em>
 * </pre>
 */
public class ConditionalNotNode extends UnaryOperationNode {

    /**
     * Create a new ConditionalNotNode.
     *
     * @param tree the logical-complement tree for this node
     * @param operand the boolean expression being negated
     */
    public ConditionalNotNode(UnaryTree tree, Node operand) {
        super(tree, operand);
        assert tree.getKind() == Kind.LOGICAL_COMPLEMENT;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitConditionalNot(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet ConditionalNotNode this) {
        return "(!" + getOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet ConditionalNotNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ConditionalNotNode)) {
            return false;
        }
        ConditionalNotNode other = (ConditionalNotNode) obj;
        return getOperand().equals(other.getOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet ConditionalNotNode this) {
        return Objects.hash(ConditionalNotNode.class, getOperand());
    }
}
