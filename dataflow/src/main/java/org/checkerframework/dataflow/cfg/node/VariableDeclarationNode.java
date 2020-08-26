package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.VariableTree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a local variable declaration:
 *
 * <pre>
 *   <em>modifier</em> <em>type</em> <em>identifier</em>;
 * </pre>
 *
 * Note: Does not have an initializer block, as that will be translated to a separate {@link
 * AssignmentNode}.
 */
public class VariableDeclarationNode extends Node {

    protected final VariableTree tree;
    protected final String name;

    // TODO: make modifier accessible

    @SuppressWarnings(
            "determinism") // using unannoated methods that require @Det, should be @PolyDet
    public VariableDeclarationNode(VariableTree t) {
        super(TreeUtils.typeOf(t));
        tree = t;
        name = tree.getName().toString();
    }

    public @PolyDet String getName(@PolyDet VariableDeclarationNode this) {
        return name;
    }

    @Override
    public @PolyDet VariableTree getTree(@PolyDet VariableDeclarationNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitVariableDeclaration(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet VariableDeclarationNode this) {
        return name;
    }

    @Override
    public @PolyDet boolean equals(
            @PolyDet VariableDeclarationNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof VariableDeclarationNode)) {
            return false;
        }
        VariableDeclarationNode other = (VariableDeclarationNode) obj;
        return getName().equals(other.getName());
    }

    @Override
    public @NonDet int hashCode(@PolyDet VariableDeclarationNode this) {
        return Objects.hash(getName());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.emptyList();
    }
}
