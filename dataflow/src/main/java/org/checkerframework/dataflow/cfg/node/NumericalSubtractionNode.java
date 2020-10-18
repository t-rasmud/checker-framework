package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import com.sun.source.tree.Tree.Kind;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the numerical subtraction:
 *
 * <pre>
 *   <em>expression</em> - <em>expression</em>
 * </pre>
 */
public class NumericalSubtractionNode extends BinaryOperationNode {

    public NumericalSubtractionNode(BinaryTree tree, Node left, Node right) {
        super(tree, left, right);
        assert tree.getKind() == Kind.MINUS;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitNumericalSubtraction(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet NumericalSubtractionNode this) {
        return "(" + getLeftOperand() + " - " + getRightOperand() + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet NumericalSubtractionNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof NumericalSubtractionNode)) {
            return false;
        }
        NumericalSubtractionNode other = (NumericalSubtractionNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet NumericalSubtractionNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
