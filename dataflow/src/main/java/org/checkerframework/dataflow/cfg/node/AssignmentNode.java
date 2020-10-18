package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.AssignmentTree;
import com.sun.source.tree.CompoundAssignmentTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import com.sun.source.tree.VariableTree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.cfg.node.AssignmentContext.AssignmentLhsContext;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for an assignment:
 *
 * <pre>
 *   <em>variable</em> = <em>expression</em>
 *   <em>expression</em> . <em>field</em> = <em>expression</em>
 *   <em>expression</em> [ <em>index</em> ] = <em>expression</em>
 * </pre>
 *
 * We allow assignments without corresponding AST {@link Tree}s.
 */
public class AssignmentNode extends Node {

    protected final Tree tree;
    protected final Node lhs;
    protected final Node rhs;

    public AssignmentNode(Tree tree, Node target, Node expression) {
        super(TreeUtils.typeOf(tree));
        assert tree instanceof AssignmentTree
                || tree instanceof VariableTree
                || tree instanceof CompoundAssignmentTree
                || tree instanceof UnaryTree;
        assert target instanceof FieldAccessNode
                || target instanceof LocalVariableNode
                || target instanceof ArrayAccessNode;
        this.tree = tree;
        this.lhs = target;
        this.rhs = expression;
        rhs.setAssignmentContext(new AssignmentLhsContext(lhs));
    }

    public @PolyDet Node getTarget(@PolyDet AssignmentNode this) {
        return lhs;
    }

    public @PolyDet Node getExpression(@PolyDet AssignmentNode this) {
        return rhs;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet AssignmentNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitAssignment(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet AssignmentNode this) {
        return getTarget() + " = " + getExpression();
    }

    @Override
    public @PolyDet boolean equals(@PolyDet AssignmentNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof AssignmentNode)) {
            return false;
        }
        AssignmentNode other = (AssignmentNode) obj;
        return getTarget().equals(other.getTarget())
                && getExpression().equals(other.getExpression());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet AssignmentNode this) {
        return Objects.hash(getTarget(), getExpression());
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(2);
        list.add(getTarget());
        list.add(getExpression());
        return list;
    }
}
