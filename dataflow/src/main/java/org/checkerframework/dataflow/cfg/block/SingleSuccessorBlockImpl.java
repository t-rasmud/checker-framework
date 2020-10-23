package org.checkerframework.dataflow.cfg.block;

import java.util.LinkedHashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store.FlowRule;

/**
 * A basic block that has at most one successor. SpecialBlockImpl extends this, but exit blocks have
 * no successor.
 */
public abstract class SingleSuccessorBlockImpl extends BlockImpl implements SingleSuccessorBlock {

    /** Internal representation of the successor. */
    protected @Nullable BlockImpl successor;

    /**
     * The initial value for the rule below says that EACH store at the end of a single successor
     * block flows to the corresponding store of the successor.
     */
    protected FlowRule flowRule = FlowRule.EACH_TO_EACH;

    protected SingleSuccessorBlockImpl(BlockType type) {
        super(type);
    }

    @Override
    public @Nullable Block getSuccessor(SingleSuccessorBlockImpl this) {
        return successor;
    }

    @Override
    public Set<@Det Block> getSuccessors(SingleSuccessorBlockImpl this) {
        Set<@Det Block> result = new LinkedHashSet<>();
        if (successor != null) {
            result.add(successor);
        }
        return result;
    }

    /**
     * Set a basic block as the successor of this block.
     *
     * @param successor the block that will be the successor of this
     */
    public void setSuccessor(SingleSuccessorBlockImpl this, BlockImpl successor) {
        this.successor = successor;
        successor.addPredecessor(this);
    }

    @Override
    public FlowRule getFlowRule(SingleSuccessorBlockImpl this) {
        return flowRule;
    }

    @Override
    public void setFlowRule(SingleSuccessorBlockImpl this, FlowRule rule) {
        flowRule = rule;
    }
}
