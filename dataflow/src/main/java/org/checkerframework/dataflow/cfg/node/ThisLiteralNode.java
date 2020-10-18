package org.checkerframework.dataflow.cfg.node;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node for a reference to 'this', either implicit or explicit.
 *
 * <pre>
 *   <em>this</em>
 * </pre>
 */
public abstract class ThisLiteralNode extends Node {

    protected ThisLiteralNode(TypeMirror type) {
        super(type);
    }

    public @PolyDet String getName(@PolyDet ThisLiteralNode this) {
        return "this";
    }

    @Override
    public @PolyDet boolean equals(@PolyDet ThisLiteralNode this, @PolyDet @Nullable Object obj) {
        return obj instanceof ThisLiteralNode;
    }

    @Override
    public @PolyDet int hashCode(@PolyDet ThisLiteralNode this) {
        return Objects.hash(getName());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
