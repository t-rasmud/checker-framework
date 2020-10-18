package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.ConditionalExpressionTree;
import com.sun.source.tree.Tree.Kind;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a conditional expression:
 *
 * <pre>
 *   <em>expression</em> ? <em>expression</em> : <em>expression</em>
 * </pre>
 */
public class TernaryExpressionNode extends Node {

    protected final ConditionalExpressionTree tree;
    protected final Node condition;
    protected final Node thenOperand;
    protected final Node elseOperand;

    public TernaryExpressionNode(
            ConditionalExpressionTree tree, Node condition, Node thenOperand, Node elseOperand) {
        super(TreeUtils.typeOf(tree));
        assert tree.getKind() == Kind.CONDITIONAL_EXPRESSION;
        this.tree = tree;
        this.condition = condition;
        this.thenOperand = thenOperand;
        this.elseOperand = elseOperand;
    }

    public @PolyDet Node getConditionOperand(@PolyDet TernaryExpressionNode this) {
        return condition;
    }

    public @PolyDet Node getThenOperand(@PolyDet TernaryExpressionNode this) {
        return thenOperand;
    }

    public @PolyDet Node getElseOperand(@PolyDet TernaryExpressionNode this) {
        return elseOperand;
    }

    @Override
    public @PolyDet ConditionalExpressionTree getTree(@PolyDet TernaryExpressionNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitTernaryExpression(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet TernaryExpressionNode this) {
        return "("
                + getConditionOperand()
                + " ? "
                + getThenOperand()
                + " : "
                + getElseOperand()
                + ")";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet TernaryExpressionNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof TernaryExpressionNode)) {
            return false;
        }
        TernaryExpressionNode other = (TernaryExpressionNode) obj;
        return getConditionOperand().equals(other.getConditionOperand())
                && getThenOperand().equals(other.getThenOperand())
                && getElseOperand().equals(other.getElseOperand());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet TernaryExpressionNode this) {
        return Objects.hash(getConditionOperand(), getThenOperand(), getElseOperand());
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(3);
        list.add(getConditionOperand());
        list.add(getThenOperand());
        list.add(getElseOperand());
        return list;
    }
}
