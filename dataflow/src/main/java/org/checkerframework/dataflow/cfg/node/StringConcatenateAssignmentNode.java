package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree;
import com.sun.source.tree.Tree.Kind;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for the string concatenation compound assignment:
 *
 * <pre>
 *   <em>variable</em> += <em>expression</em>
 * </pre>
 */
public class StringConcatenateAssignmentNode extends Node {
    protected final Tree tree;
    protected final Node left;
    protected final Node right;

    public StringConcatenateAssignmentNode(Tree tree, Node left, Node right) {
        super(TreeUtils.typeOf(tree));
        assert tree.getKind() == Kind.PLUS_ASSIGNMENT;
        this.tree = tree;
        this.left = left;
        this.right = right;
    }

    public @PolyDet Node getLeftOperand(@PolyDet StringConcatenateAssignmentNode this) {
        return left;
    }

    public @PolyDet Node getRightOperand(@PolyDet StringConcatenateAssignmentNode this) {
        return right;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet StringConcatenateAssignmentNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitStringConcatenateAssignment(this, p);
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(2);
        list.add(getLeftOperand());
        list.add(getRightOperand());
        return list;
    }

    @Override
    public @PolyDet String toString(@PolyDet StringConcatenateAssignmentNode this) {
        return "(" + getLeftOperand() + " += " + getRightOperand() + ")";
    }

    @Override
    @SuppressWarnings("determinism") // calling equals on two @PolyDet returns @NonDet
    public boolean equals(
            @PolyDet StringConcatenateAssignmentNode this, @PolyDet @Nullable Object obj) {
        if (obj == null || !(obj instanceof StringConcatenateAssignmentNode)) {
            return false;
        }
        StringConcatenateAssignmentNode other = (StringConcatenateAssignmentNode) obj;
        return getLeftOperand().equals(other.getLeftOperand())
                && getRightOperand().equals(other.getRightOperand());
    }

    @Override
    public @NonDet int hashCode(@PolyDet StringConcatenateAssignmentNode this) {
        return Objects.hash(getLeftOperand(), getRightOperand());
    }
}
