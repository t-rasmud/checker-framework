package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree.Kind;
import com.sun.source.tree.UnaryTree;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the bitwise complement operation:
 *
 * <pre>
 *   ~ <em>expression</em>
 * </pre>
 */
public class BitwiseComplementNode extends UnaryOperationNode {

    public BitwiseComplementNode(UnaryTree tree, Node operand) {
        super(tree, operand);
        assert tree.getKind() == Kind.BITWISE_COMPLEMENT;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitBitwiseComplement(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet BitwiseComplementNode this) {
        return "(~ " + getOperand() + ")";
    }

    @Override
    @SuppressWarnings("determinism") // overriding Object method
    public @PolyDet boolean equals(
            @PolyDet BitwiseComplementNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof BitwiseComplementNode)) {
            return false;
        }
        BitwiseComplementNode other = (BitwiseComplementNode) obj;
        return getOperand().equals(other.getOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet BitwiseComplementNode this) {
        return Objects.hash(BitwiseComplementNode.class, getOperand());
    }
}
