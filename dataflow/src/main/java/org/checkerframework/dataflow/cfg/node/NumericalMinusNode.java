package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.UnaryTree;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the unary minus operation:
 *
 * <pre>
 *   - <em>expression</em>
 * </pre>
 */
public class NumericalMinusNode extends UnaryOperationNode {

    public NumericalMinusNode(UnaryTree tree, Node operand) {
        super(tree, operand);
        assert tree.getKind() == Kind.UNARY_MINUS;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitNumericalMinus(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet NumericalMinusNode this) {
        return "(- " + getOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet NumericalMinusNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof NumericalMinusNode)) {
            return false;
        }
        NumericalMinusNode other = (NumericalMinusNode) obj;
        return getOperand().equals(other.getOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet NumericalMinusNode this) {
        return Objects.hash(NumericalMinusNode.class, getOperand());
    }
}
