package org.checkerframework.dataflow.cfg.block;

import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.qual.Pure;
import org.plumelib.util.UniqueId;

/** Represents a basic block in a control flow graph. */
public interface Block extends UniqueId {

    /** The types of basic blocks. */
    public static enum BlockType {

        /** A regular basic block. */
        REGULAR_BLOCK,

        /** A conditional basic block. */
        CONDITIONAL_BLOCK,

        /** A special basic block. */
        SPECIAL_BLOCK,

        /** A basic block that can throw an exception. */
        EXCEPTION_BLOCK,
    }

    /**
     * Returns the type of this basic block.
     *
     * @return the type of this basic block
     */
    @Det BlockType getType(@Det Block this);

    /**
     * Returns the predecessors of this basic block.
     *
     * @return the predecessors of this basic block
     */
    @Det Set<@Det Block> getPredecessors(@Det Block this);

    /**
     * Returns the successors of this basic block.
     *
     * @return the successors of this basic block
     */
    @Det Set<@Det Block> getSuccessors(@Det Block this);

    /**
     * Returns the nodes contained within this basic block. The list may be empty.
     *
     * <p>The following invariant holds.
     *
     * <pre>
     * forall n in getNodes() :: n.getBlock() == this
     * </pre>
     *
     * @return the nodes contained within this basic block
     */
    @Pure
    @Det List<@Det Node> getNodes(@Det Block this);

    /**
     * Returns the last node of this block, or null if none.
     *
     * @return the last node of this block or {@code null}
     */
    @Det @Nullable Node getLastNode(@Det Block this);
}
