package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the bitwise or logical (single bit) xor operation:
 *
 * <pre>
 *   <em>expression</em> ^ <em>expression</em>
 * </pre>
 */
public class BitwiseXorNode extends BinaryOperationNode {

    public BitwiseXorNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.XOR;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitBitwiseXor(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet BitwiseXorNode this) {
        return "(" + getLeftOperand() + " ^ " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet BitwiseXorNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof BitwiseXorNode)) {
            return false;
        }
        BitwiseXorNode other = (BitwiseXorNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet BitwiseXorNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
