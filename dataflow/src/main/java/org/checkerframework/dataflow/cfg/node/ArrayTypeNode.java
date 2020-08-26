package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.ArrayTypeTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.util.Types;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node representing a array type used in an expression such as a field access.
 *
 * <p><em>type</em> .class
 */
public class ArrayTypeNode extends Node {

    protected final ArrayTypeTree tree;

    /** For Types.isSameType. */
    protected final Types types;

    public ArrayTypeNode(ArrayTypeTree tree, Types types) {
        super(TreeUtils.typeOf(tree));
        this.tree = tree;
        this.types = types;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet ArrayTypeNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitArrayType(this, p);
    }

    @Override
    @SuppressWarnings("determinism") // imprecise library annotation: trees
    public @PolyDet String toString(@PolyDet ArrayTypeNode this) {
        return tree.toString();
    }

    @Override
    @SuppressWarnings("determinism") // imprecise library annotation: trees
    public @PolyDet boolean equals(@PolyDet ArrayTypeNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ArrayTypeNode)) {
            return false;
        }
        ArrayTypeNode other = (ArrayTypeNode) obj;
        return types.isSameType(getType(), other.getType());
    }

    @Override
    public @NonDet int hashCode(@PolyDet ArrayTypeNode this) {
        return Objects.hash(getType());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
