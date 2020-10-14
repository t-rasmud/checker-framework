package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Types;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * MarkerNodes are no-op Nodes used for debugging information. They can hold a Tree and a message,
 * which will be part of the String representation of the MarkerNode.
 *
 * <p>An example use case for MarkerNodes is representing switch statements.
 */
public class MarkerNode extends Node {

    protected final @Nullable Tree tree;
    protected final @NonDet String message;

    // message may be @NonDet because in CFGBuilder.java, the SwitchBuilder#build method passes a
    // hash code.
    public MarkerNode(@Nullable Tree tree, @NonDet String message, Types types) {
        super(types.getNoType(TypeKind.NONE));
        this.tree = tree;
        this.message = message;
    }

    public @NonDet String getMessage(@PolyDet MarkerNode this) {
        return message;
    }

    @Override
    public @PolyDet @Nullable Tree getTree(@PolyDet MarkerNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitMarker(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet MarkerNode this) {
        StringBuilder sb = new @NonDet StringBuilder();
        sb.append("marker (");
        sb.append(message);
        sb.append(")");
        return sb.toString();
    }

    @Override
    @SuppressWarnings(
            "determinism") // valid rule relaxation: contents of message field is @NonDet, but
    // result of comparisons between runs is always @Det
    public @PolyDet boolean equals(@PolyDet MarkerNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof MarkerNode)) {
            return false;
        }
        MarkerNode other = (MarkerNode) obj;
        return Objects.equals(getTree(), other.getTree())
                && getMessage().equals(other.getMessage());
    }

    @Override
    public @NonDet int hashCode(@PolyDet MarkerNode this) {
        return Objects.hash(tree, getMessage());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
