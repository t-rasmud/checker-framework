package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for a boolean literal:
 *
 * <pre>
 *   <em>true</em>
 *   <em>false</em>
 * </pre>
 */
public class BooleanLiteralNode extends ValueLiteralNode {

    /**
     * Create a new BooleanLiteralNode.
     *
     * @param t the tree for the literal value
     */
    public BooleanLiteralNode(LiteralTree t) {
        super(t);
        assert t.getKind() == Tree.Kind.BOOLEAN_LITERAL;
    }

    @Override
    @SuppressWarnings("determinism") // calling method on external class requires @Det
    public @PolyDet Boolean getValue(@PolyDet BooleanLiteralNode this) {
        return (Boolean) tree.getValue();
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitBooleanLiteral(this, p);
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet BooleanLiteralNode this, @PolyDet @Nullable Object obj) {
        // test that obj is a BooleanLiteralNode
        if (!(obj instanceof BooleanLiteralNode)) {
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
