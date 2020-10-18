package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the cast operator:
 *
 * <p>(<em>Point</em>) <em>x</em>
 */
public class TypeCastNode extends Node {

    protected final Tree tree;
    protected final Node operand;

    /** For Types.isSameType. */
    protected final Types types;

    public TypeCastNode(Tree tree, Node operand, TypeMirror type, Types types) {
        super(type);
        this.tree = tree;
        this.operand = operand;
        this.types = types;
    }

    public @PolyDet Node getOperand(@PolyDet TypeCastNode this) {
        return operand;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet TypeCastNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitTypeCast(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet TypeCastNode this) {
        return "(" + getType() + ")" + getOperand();
    }

    @Override
    public @PolyDet boolean equals(@PolyDet TypeCastNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof TypeCastNode)) {
            return false;
        }
        TypeCastNode other = (TypeCastNode) obj;
        return getOperand().equals(other.getOperand())
                && types.isSameType(getType(), other.getType());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet TypeCastNode this) {
        return Objects.hash(getType(), getOperand());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.singletonList(getOperand());
    }
}
