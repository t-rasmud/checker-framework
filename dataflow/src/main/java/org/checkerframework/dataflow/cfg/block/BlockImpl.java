package org.checkerframework.dataflow.cfg.block;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;

/** Base class of the {@link Block} implementation hierarchy. */
public abstract class BlockImpl implements Block {

    /** The type of this basic block. */
    protected final BlockType type;

    /** The set of predecessors. */
    protected final @Det Set<BlockImpl> predecessors;

    /** The unique ID for the next-created object. */
    static final AtomicLong nextUid = new AtomicLong(0);
    /** The unique ID of this object. */
    final long uid = nextUid.getAndIncrement();
    /**
     * Returns the unique ID of this object.
     *
     * @return the unique ID of this object.
     */
    @Override
    public @Det long getUid(@UnknownInitialization @PolyDet BlockImpl this) {
        return uid;
    }

    /**
     * Create a new BlockImpl.
     *
     * @param type the type of this basic block
     */
    protected BlockImpl(BlockType type) {
        this.type = type;
        this.predecessors = new LinkedHashSet<>();
    }

    @Override
    public @Det BlockType getType(@Det BlockImpl this) {
        return type;
    }

    @Override
    public @Det Set<@Det Block> getPredecessors(@Det BlockImpl this) {
        // Not "Collections.unmodifiableSet(predecessors)" which has nondeterministic iteration
        // order.
        return new LinkedHashSet<>(predecessors);
    }

    public void addPredecessor(@Det BlockImpl this, @Det BlockImpl pred) {
        predecessors.add(pred);
    }

    public void removePredecessor(@Det BlockImpl this, @Det BlockImpl pred) {
        predecessors.remove(pred);
    }
}
