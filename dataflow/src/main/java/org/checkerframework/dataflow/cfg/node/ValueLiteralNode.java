package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.LiteralTree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a literals that have some form of value:
 *
 * <ul>
 *   <li>integer literal
 *   <li>long literal
 *   <li>char literal
 *   <li>string literal
 *   <li>float literal
 *   <li>double literal
 *   <li>boolean literal
 *   <li>null literal
 * </ul>
 */
public abstract class ValueLiteralNode extends Node {

    /** The tree for the value literal. */
    protected final LiteralTree tree;

    /**
     * Returns the value of the literal, null for the null literal.
     *
     * @return the value of the literal, null for the null literal
     */
    public abstract @PolyDet @Nullable Object getValue(@PolyDet ValueLiteralNode this);

    protected ValueLiteralNode(LiteralTree tree) {
        super(TreeUtils.typeOf(tree));
        this.tree = tree;
    }

    @Override
    public @PolyDet LiteralTree getTree(@PolyDet ValueLiteralNode this) {
        return tree;
    }

    @Override
    public @PolyDet String toString(@PolyDet ValueLiteralNode this) {
        return String.valueOf(getValue());
    }

    /** Compare the value of this nodes. */
    @Override
    @SuppressWarnings("determinism") // Object.equals is imprecise
    public @PolyDet boolean equals(@PolyDet ValueLiteralNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ValueLiteralNode)) {
            return false;
        }
        ValueLiteralNode other = (ValueLiteralNode) obj;
        Object val = getValue();
        Object otherVal = other.getValue();
        return Objects.equals(val, otherVal);
    }

    @Override
    @SuppressWarnings("determinism") // calling method on external class requires @Det
    public @NonDet int hashCode(@PolyDet ValueLiteralNode this) {
        // value might be null
        return Objects.hash(this.getClass(), getValue());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
