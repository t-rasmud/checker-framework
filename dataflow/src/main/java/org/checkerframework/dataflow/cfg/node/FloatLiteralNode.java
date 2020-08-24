package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for a float literal. For example:
 *
 * <pre>
 *   <em>8.0f</em>
 *   <em>6.022137e+23F</em>
 * </pre>
 */
public class FloatLiteralNode extends ValueLiteralNode {

    /**
     * Create a new FloatLiteralNode.
     *
     * @param t the tree for the literal value
     */
    public FloatLiteralNode(LiteralTree t) {
        super(t);
        assert t.getKind() == Tree.Kind.FLOAT_LITERAL;
    }

    @Override
    @SuppressWarnings("determinism") // calling method on external class requires @Det
    public Float getValue(@PolyDet FloatLiteralNode this) {
        return (Float) tree.getValue();
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitFloatLiteral(this, p);
    }

    @Override
    public @PolyDet boolean equals(@PolyDet FloatLiteralNode this, @PolyDet @Nullable Object obj) {
        // test that obj is a FloatLiteralNode
        if (!(obj instanceof FloatLiteralNode)) {
            return false;
        }
        // super method compares values
        return super.equals(obj);
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
