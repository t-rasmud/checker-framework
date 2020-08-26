package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TypesUtils;

/**
 * A node for the narrowing primitive conversion operation. See JLS 5.1.3 for the definition of
 * narrowing primitive conversion.
 *
 * <p>A {@link NarrowingConversionNode} does not correspond to any tree node in the parsed AST. It
 * is introduced when a value of some primitive type appears in a context that requires a different
 * primitive with more bits of precision.
 */
public class NarrowingConversionNode extends Node {

    protected final Tree tree;
    protected final Node operand;

    public NarrowingConversionNode(Tree tree, Node operand, TypeMirror type) {
        super(type);
        assert TypesUtils.isPrimitive(type) : "non-primitive type in narrowing conversion";
        this.tree = tree;
        this.operand = operand;
    }

    public @PolyDet Node getOperand(@PolyDet NarrowingConversionNode this) {
        return operand;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet NarrowingConversionNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitNarrowingConversion(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet NarrowingConversionNode this) {
        return "NarrowingConversion(" + getOperand() + ", " + type + ")";
    }

    @Override
    @SuppressWarnings(
            "determinism") // using unannoated methods that require @Det, should be @PolyDet
    public @PolyDet boolean equals(
            @PolyDet NarrowingConversionNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof NarrowingConversionNode)) {
            return false;
        }
        NarrowingConversionNode other = (NarrowingConversionNode) obj;
        return getOperand().equals(other.getOperand())
                && TypesUtils.areSamePrimitiveTypes(getType(), other.getType());
    }

    @Override
    public @NonDet int hashCode(@PolyDet NarrowingConversionNode this) {
        return Objects.hash(NarrowingConversionNode.class, getOperand());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.singletonList(getOperand());
    }
}
