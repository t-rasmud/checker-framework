package org.checkerframework.dataflow.cfg.block;

import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.cfg.node.Node;

/** The implementation of a {@link SpecialBlock}. */
public class SpecialBlockImpl extends SingleSuccessorBlockImpl implements SpecialBlock {

    /** The type of this special basic block. */
    protected final SpecialBlockType specialType;

    public SpecialBlockImpl(SpecialBlockType type) {
        super(BlockType.SPECIAL_BLOCK);
        this.specialType = type;
    }

    @Override
    public @Det SpecialBlockType getSpecialType(@Det SpecialBlockImpl this) {
        return specialType;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an empty list.
     */
    @Override
    public @Det List<@Det Node> getNodes(@Det SpecialBlockImpl this) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable @Det Node getLastNode(@Det SpecialBlockImpl this) {
        return null;
    }

    @Override
    public @PolyDet String toString(@PolyDet SpecialBlockImpl this) {
        return "SpecialBlock(" + specialType + ")";
    }
}
