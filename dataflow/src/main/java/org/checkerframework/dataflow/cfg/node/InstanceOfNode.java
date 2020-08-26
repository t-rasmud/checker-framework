package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.InstanceOfTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for the instanceof operator:
 *
 * <p><em>x</em> instanceof <em>Point</em>
 */
public class InstanceOfNode extends Node {

    /** The value being tested. */
    protected final Node operand;

    /** The reference type being tested against. */
    protected final TypeMirror refType;

    /** The tree associated with this node. */
    protected final InstanceOfTree tree;

    /** For Types.isSameType. */
    protected final Types types;

    /** Create an InstanceOfNode. */
    public InstanceOfNode(Tree tree, Node operand, TypeMirror refType, Types types) {
        super(types.getPrimitiveType(TypeKind.BOOLEAN));
        assert tree.getKind() == Tree.Kind.INSTANCE_OF;
        this.tree = (InstanceOfTree) tree;
        this.operand = operand;
        this.refType = refType;
        this.types = types;
    }

    public @PolyDet Node getOperand(@PolyDet InstanceOfNode this) {
        return operand;
    }

    /** The reference type being tested against. */
    public @PolyDet TypeMirror getRefType(@PolyDet InstanceOfNode this) {
        return refType;
    }

    @Override
    public @PolyDet InstanceOfTree getTree(@PolyDet InstanceOfNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitInstanceOf(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet InstanceOfNode this) {
        return "(" + getOperand() + " instanceof " + getRefType() + ")";
    }

    @Override
    @SuppressWarnings("determinism") // calling method on external class requires @Det
    public @PolyDet boolean equals(@PolyDet InstanceOfNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof InstanceOfNode)) {
            return false;
        }
        InstanceOfNode other = (InstanceOfNode) obj;
        // TODO: TypeMirror.equals may be too restrictive.
        // Check whether Types.isSameType is the better comparison.
        return getOperand().equals(other.getOperand())
                && types.isSameType(getRefType(), other.getRefType());
    }

    @Override
    public @NonDet int hashCode(@PolyDet InstanceOfNode this) {
        return Objects.hash(InstanceOfNode.class, getOperand());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.singletonList(getOperand());
    }
}
