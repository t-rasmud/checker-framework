package org.checkerframework.dataflow.cfg.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.cfg.node.Node;

/** Implementation of a regular basic block. */
public class RegularBlockImpl extends SingleSuccessorBlockImpl implements RegularBlock {

    /** Internal representation of the contents. */
    protected final List<Node> contents;

    /**
     * Initialize an empty basic block to be filled with contents and linked to other basic blocks
     * later.
     */
    public RegularBlockImpl() {
        super(BlockType.REGULAR_BLOCK);
        contents = new ArrayList<>();
    }

    /** Add a node to the contents of this basic block. */
    public void addNode(RegularBlockImpl this, Node t) {
        contents.add(t);
        t.setBlock(this);
    }

    /** Add multiple nodes to the contents of this basic block. */
    public void addNodes(RegularBlockImpl this, List<? extends Node> ts) {
        for (Node t : ts) {
            addNode(t);
        }
    }

    @SuppressWarnings("deprecation") // implementation of deprecated method in interface
    @Override
    public List<@Det Node> getContents(RegularBlockImpl this) {
        return getNodes();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an non-empty list.
     */
    @Override
    public List<@Det Node> getNodes(RegularBlockImpl this) {
        return Collections.unmodifiableList(contents);
    }

    @Override
    public @Nullable Node getLastNode(RegularBlockImpl this) {
        return contents.get(contents.size() - 1);
    }

    @Override
    public @Nullable BlockImpl getRegularSuccessor(RegularBlockImpl this) {
        return successor;
    }

    @Override
    public @PolyDet String toString(@PolyDet RegularBlockImpl this) {
        return "RegularBlock(" + contents + ")";
    }

    @Override
    public boolean isEmpty(RegularBlockImpl this) {
        return contents.isEmpty();
    }
}
