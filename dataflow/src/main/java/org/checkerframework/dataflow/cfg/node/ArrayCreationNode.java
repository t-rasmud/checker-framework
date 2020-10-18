package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.plumelib.util.UtilPlume;

/**
 * A node for new array creation.
 *
 * <pre>
 *   <em>new type [1][2]</em>
 *   <em>new type [] = { expr1, expr2, ... }</em>
 * </pre>
 */
public class ArrayCreationNode extends Node {

    /** The tree is null when an array is created for variable arity method calls. */
    protected final @Nullable NewArrayTree tree;

    /**
     * The length of this list is the number of dimensions in the array. Each element is the size of
     * the given dimension.
     */
    protected final List<Node> dimensions;

    protected final List<Node> initializers;

    public ArrayCreationNode(
            @Nullable NewArrayTree tree,
            TypeMirror type,
            List<Node> dimensions,
            List<Node> initializers) {
        super(type);
        this.tree = tree;
        this.dimensions = dimensions;
        this.initializers = initializers;
    }

    public @PolyDet List<Node> getDimensions(@PolyDet ArrayCreationNode this) {
        return dimensions;
    }

    public Node getDimension(int i) {
        return dimensions.get(i);
    }

    public @PolyDet List<Node> getInitializers(@PolyDet ArrayCreationNode this) {
        return initializers;
    }

    public Node getInitializer(int i) {
        return initializers.get(i);
    }

    @Override
    public @PolyDet @Nullable Tree getTree(@PolyDet ArrayCreationNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitArrayCreation(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet ArrayCreationNode this) {
        StringBuilder sb = new @PolyDet StringBuilder();
        sb.append("new " + type);
        if (!dimensions.isEmpty()) {
            sb.append(" (");
            sb.append(UtilPlume.join(", ", dimensions));
            sb.append(")");
        }
        if (!initializers.isEmpty()) {
            sb.append(" = {");
            sb.append(UtilPlume.join(", ", initializers));
            sb.append("}");
        }
        return sb.toString();
    }

    @Override
    public @PolyDet("up") boolean equals(
            @PolyDet ArrayCreationNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ArrayCreationNode)) {
            return false;
        }
        ArrayCreationNode other = (ArrayCreationNode) obj;

        return getDimensions().equals(other.getDimensions())
                && getInitializers().equals(other.getInitializers());
    }

    @Override
    public @PolyDet int hashCode(@PolyDet ArrayCreationNode this) {
        return Objects.hash(dimensions, initializers);
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(dimensions.size() + initializers.size());
        list.addAll(dimensions);
        list.addAll(initializers);
        return list;
    }
}
