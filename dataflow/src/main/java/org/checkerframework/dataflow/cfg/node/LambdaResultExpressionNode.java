package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.ExpressionTree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.util.Types;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/** A node for the single expression body of a single expression lambda. */
public class LambdaResultExpressionNode extends Node {

    protected final ExpressionTree tree;
    protected final @Nullable Node result;

    public LambdaResultExpressionNode(ExpressionTree t, @Nullable Node result, Types types) {
        super(TreeUtils.typeOf(t));
        this.result = result;
        tree = t;
    }

    /**
     * Returns the final node of the CFG corresponding to the lambda expression body (see {@link
     * #getTree()}).
     */
    public @Nullable Node getResult() {
        return result;
    }

    /**
     * Returns the {@link ExpressionTree} corresponding to the body of a lambda expression with an
     * expression body (e.g. X for ({@code o -> X}) where X is an expression and not a {...} block).
     */
    @Override
    public @PolyDet ExpressionTree getTree(@PolyDet LambdaResultExpressionNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitLambdaResultExpression(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet LambdaResultExpressionNode this) {
        if (result != null) {
            return "-> " + result;
        }
        return "-> ()";
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet LambdaResultExpressionNode this, @PolyDet @Nullable Object obj) {
        // No need to compare tree, since in a well-formed LambdaResultExpressionNode, result will
        // be the same only when tree is the same (this is similar to ReturnNode).
        if (!(obj instanceof LambdaResultExpressionNode)) {
            return false;
        }
        LambdaResultExpressionNode other = (LambdaResultExpressionNode) obj;
        return Objects.equals(result, other.result);
    }

    @Override
    public @PolyDet int hashCode(@PolyDet LambdaResultExpressionNode this) {
        // No need to incorporate tree, since in a well-formed LambdaResultExpressionNode, result
        // will be the same only when tree is the same (this is similar to ReturnNode).
        return Objects.hash(LambdaResultExpressionNode.class, result);
    }

    @Override
    public Collection<Node> getOperands() {
        if (result == null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(result);
        }
    }
}
