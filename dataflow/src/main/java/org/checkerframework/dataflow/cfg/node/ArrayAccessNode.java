package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.ArrayAccessTree;
import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for an array access:
 *
 * <pre>
 *   <em>arrayref</em> [ <em>index</em> ]
 * </pre>
 *
 * We allow array accesses without corresponding AST {@link Tree}s.
 */
public class ArrayAccessNode extends Node {

    protected final Tree tree;
    protected final Node array;
    protected final Node index;

    public ArrayAccessNode(Tree t, Node array, Node index) {
        super(TreeUtils.typeOf(t));
        assert t instanceof ArrayAccessTree;
        this.tree = t;
        this.array = array;
        this.index = index;
    }

    public @PolyDet Node getArray(@PolyDet ArrayAccessNode this) {
        return array;
    }

    public @PolyDet Node getIndex(@PolyDet ArrayAccessNode this) {
        return index;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet ArrayAccessNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitArrayAccess(this, p);
    }

    @Override
    public @NonDet String toString(@PolyDet ArrayAccessNode this) {
        String base = getArray().toString() + "[" + getIndex() + "]";
        return base;
    }

    @Override
    public @PolyDet boolean equals(@PolyDet ArrayAccessNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ArrayAccessNode)) {
            return false;
        }
        ArrayAccessNode other = (ArrayAccessNode) obj;
        return getArray().equals(other.getArray()) && getIndex().equals(other.getIndex());
    }

    @Override
    public @NonDet int hashCode(@PolyDet ArrayAccessNode this) {
        return Objects.hash(getArray(), getIndex());
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(2);
        list.add(getArray());
        list.add(getIndex());
        return list;
    }
}
