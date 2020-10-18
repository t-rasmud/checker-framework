package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.LiteralTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for an string literal. For example:
 *
 * <pre>
 *   <em>"abc"</em>
 * </pre>
 */
public class StringLiteralNode extends ValueLiteralNode {

    /**
     * Create a new StringLiteralNode.
     *
     * @param t the tree for the literal value
     */
    public StringLiteralNode(LiteralTree t) {
        super(t);
        assert t.getKind() == Tree.Kind.STRING_LITERAL;
    }

    @Override
    public @PolyDet String getValue(@PolyDet StringLiteralNode this) {
        return (String) tree.getValue();
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitStringLiteral(this, p);
    }

    @Override
    public @PolyDet boolean equals(@PolyDet StringLiteralNode this, @PolyDet @Nullable Object obj) {
        // test that obj is a StringLiteralNode
        if (!(obj instanceof StringLiteralNode)) {
            return false;
        }
        // super method compares values
        return super.equals(obj);
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }

    @Override
    public @PolyDet String toString(@PolyDet StringLiteralNode this) {
        return "\"" + super.toString() + "\"";
    }
}
