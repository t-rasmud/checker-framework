package org.checkerframework.dataflow.cfg.builder;

import java.util.Map;
import java.util.Set;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.dataflow.cfg.builder.ExtendedNode.ExtendedNodeType;
import org.checkerframework.dataflow.cfg.node.Node;

/** An extended node of type {@code EXCEPTION_NODE}. */
class NodeWithExceptionsHolder extends ExtendedNode {

    /** The node to hold. */
    protected final Node node;

    /**
     * Map from exception type to labels of successors that may be reached as a result of that
     * exception.
     */
    protected final @OrderNonDet Map<@Det TypeMirror, @OrderNonDet Set<Label>> exceptions;

    /**
     * Construct a NodeWithExceptionsHolder for the given node and exceptions.
     *
     * @param node the node to hold
     * @param exceptions the exceptions to hold
     */
    public NodeWithExceptionsHolder(
            Node node, @OrderNonDet Map<@Det TypeMirror, @OrderNonDet Set<Label>> exceptions) {
        super(ExtendedNodeType.EXCEPTION_NODE);
        this.node = node;
        this.exceptions = exceptions;
    }

    /**
     * Get the exceptions for the node.
     *
     * @return exceptions for the node
     */
    public @OrderNonDet Map<@Det TypeMirror, @OrderNonDet Set<Label>> getExceptions() {
        return exceptions;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public @PolyDet String toString(@PolyDet NodeWithExceptionsHolder this) {
        return "NodeWithExceptionsHolder(" + node + ")";
    }

    @Override
    public @PolyDet String toStringDebug() {
        return "NodeWithExceptionsHolder(" + node.toStringDebug() + ")";
    }
}
