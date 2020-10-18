package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.Tree;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;

/** A node to model the implicit {@code this}, e.g., in a field access. */
public class ImplicitThisLiteralNode extends ThisLiteralNode {

    public ImplicitThisLiteralNode(TypeMirror type) {
        super(type);
    }

    @Override
    public @PolyDet @Nullable Tree getTree(@PolyDet ImplicitThisLiteralNode this) {
        return null;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitImplicitThisLiteral(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet ImplicitThisLiteralNode this) {
        return "(" + getName() + ")";
    }
}
