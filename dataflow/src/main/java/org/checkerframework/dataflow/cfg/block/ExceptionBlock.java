package org.checkerframework.dataflow.cfg.block;

import java.util.Map;
import java.util.Set;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.qual.Pure;

/**
 * Represents a basic block that contains exactly one {@link Node} which can throw an exception.
 * This block has exactly one non-exceptional successor, and one or more exceptional successors.
 *
 * <p>The following invariant holds.
 *
 * <pre>
 * getNode().getBlock() == this
 * </pre>
 */
public interface ExceptionBlock extends SingleSuccessorBlock {

    /**
     * Returns the node of this block.
     *
     * @return the node of this block
     */
    @Pure
    @Det Node getNode(@Det ExceptionBlock this);

    /**
     * Returns the list of exceptional successor blocks as an unmodifiable map.
     *
     * @return the list of exceptional successor blocks as an unmodifiable map
     */
    @Pure
    @Det Map<@Det TypeMirror, @Det Set<@Det Block>> getExceptionalSuccessors(@Det ExceptionBlock this);
}
