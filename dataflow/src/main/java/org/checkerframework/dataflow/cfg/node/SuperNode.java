package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a reference to 'super'.
 *
 * <pre>
 *   <em>super</em>
 * </pre>
 */
public class SuperNode extends Node {

    protected final Tree tree;

    public SuperNode(Tree t) {
        super(TreeUtils.typeOf(t));
        assert t instanceof IdentifierTree && ((IdentifierTree) t).getName().contentEquals("super");
        tree = t;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet SuperNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitSuper(this, p);
    }

    public @PolyDet String getName(@PolyDet SuperNode this) {
        return "super";
    }

    @Override
    public @PolyDet String toString(@PolyDet SuperNode this) {
        return getName();
    }

    @Override
    public @PolyDet boolean equals(@PolyDet SuperNode this, @PolyDet @Nullable Object obj) {
        return obj instanceof SuperNode;
    }

    @Override
    public @PolyDet int hashCode(@PolyDet SuperNode this) {
        return Objects.hash(getName());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
