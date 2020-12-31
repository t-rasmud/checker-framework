package org.checkerframework.dataflow.cfg.block;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;

/** Base class of the {@link Block} implementation hierarchy. */
public abstract class BlockImpl implements Block {

    /** The type of this basic block. */
    protected final BlockType type;

    /** The set of predecessors. */
    protected final Set<BlockImpl> predecessors;

    /** The unique ID for the next-created object. */
    static final AtomicLong nextUid = new AtomicLong(0);
    /** The unique ID of this object. */
    final long uid = nextUid.getAndIncrement();
    /**
     * Returns the unique ID of this object.
     *
     * @return the unique ID of this object
     */
    @Override
    public @PolyDet long getUid(@UnknownInitialization @PolyDet BlockImpl this) {
        return uid;
    }

    /**
     * Create a new BlockImpl.
     *
     * @param type the type of this basic block
     */
    // true positive; `predecessors` is declared as a Det Set but assigned an OrderNonDet HashSet
    // Fixed: https://github.com/typetools/checker-framework/commit/67702a13926eb3d17bde9cdbc7d4c66b7c570ea0
    protected BlockImpl(BlockType type) {
        this.type = type;
        this.predecessors = new HashSet<>();
    }

    @Override
    public BlockType getType(BlockImpl this) {
        return type;
    }

    @Override
    // true positive; declared return type is a Det Set but OrderNonDet HashSet is returned
    // Fixed: https://github.com/typetools/checker-framework/commit/67702a13926eb3d17bde9cdbc7d4c66b7c570ea0
    public Set<@Det Block> getPredecessors(BlockImpl this) {
        // Not "Collections.unmodifiableSet(predecessors)" which has nondeterministic iteration
        // order.
        return new HashSet<>(predecessors);
    }

    public void addPredecessor(BlockImpl this, BlockImpl pred) {
        predecessors.add(pred);
    }

    public void removePredecessor(BlockImpl this, BlockImpl pred) {
        predecessors.remove(pred);
    }
}
