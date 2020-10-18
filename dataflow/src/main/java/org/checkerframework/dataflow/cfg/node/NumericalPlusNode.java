package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.UnaryTree;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the unary plus operation:
 *
 * <pre>
 *   + <em>expression</em>
 * </pre>
 */
public class NumericalPlusNode extends UnaryOperationNode {

    public NumericalPlusNode(UnaryTree tree, Node operand) {
        super(tree, operand);
        assert tree.getKind() == Kind.UNARY_PLUS;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitNumericalPlus(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet NumericalPlusNode this) {
        return "(+ " + getOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet NumericalPlusNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof NumericalPlusNode)) {
            return false;
        }
        NumericalPlusNode other = (NumericalPlusNode) obj;
        return getOperand().equals(other.getOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet NumericalPlusNode this) {
        return Objects.hash(NumericalPlusNode.class, getOperand());
    }
}
