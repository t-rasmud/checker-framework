package org.checkerframework.dataflow.cfg.block;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.Store.FlowRule;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.javacutil.BugInCF;

/** Implementation of a conditional basic block. */
public class ConditionalBlockImpl extends BlockImpl implements ConditionalBlock {

    /** Successor of the then branch. */
    protected @Nullable BlockImpl thenSuccessor;

    /** Successor of the else branch. */
    protected @Nullable BlockImpl elseSuccessor;

    /**
     * The initial values for the rules below say that the THEN store before a conditional block
     * flows to BOTH of the stores of the then successor, while the ELSE store before a conditional
     * block flows to BOTH of the stores of the else successor.
     */
    protected Store.FlowRule thenFlowRule = Store.FlowRule.THEN_TO_BOTH;

    protected Store.FlowRule elseFlowRule = Store.FlowRule.ELSE_TO_BOTH;

    /**
     * Initialize an empty conditional basic block to be filled with contents and linked to other
     * basic blocks later.
     */
    public ConditionalBlockImpl() {
        super(BlockType.CONDITIONAL_BLOCK);
    }

    /** Set the then branch successor. */
    public void setThenSuccessor(@Det ConditionalBlockImpl this, @Det BlockImpl b) {
        thenSuccessor = b;
        b.addPredecessor(this);
    }

    /** Set the else branch successor. */
    public void setElseSuccessor(@Det ConditionalBlockImpl this, @Det BlockImpl b) {
        elseSuccessor = b;
        b.addPredecessor(this);
    }

    @Override
    public @Det Block getThenSuccessor(@Det ConditionalBlockImpl this) {
        if (thenSuccessor == null) {
            throw new BugInCF(
                    "Requested thenSuccessor for conditional block before initialization");
        }
        return thenSuccessor;
    }

    @Override
    public @Det Block getElseSuccessor(@Det ConditionalBlockImpl this) {
        if (elseSuccessor == null) {
            throw new BugInCF(
                    "Requested elseSuccessor for conditional block before initialization");
        }
        return elseSuccessor;
    }

    @Override
    public @Det Set<Block> getSuccessors(@Det ConditionalBlockImpl this) {
        Set<@Det Block> result = new LinkedHashSet<>(2);
        result.add(getThenSuccessor());
        result.add(getElseSuccessor());
        return result;
    }

    @Override
    public @Det FlowRule getThenFlowRule(@Det ConditionalBlockImpl this) {
        return thenFlowRule;
    }

    @Override
    public @Det FlowRule getElseFlowRule(@Det ConditionalBlockImpl this) {
        return elseFlowRule;
    }

    @Override
    public void setThenFlowRule(@Det ConditionalBlockImpl this, @Det FlowRule rule) {
        thenFlowRule = rule;
    }

    @Override
    public void setElseFlowRule(@Det ConditionalBlockImpl this, @Det FlowRule rule) {
        elseFlowRule = rule;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns an empty list.
     */
    @Override
    public @Det List<@Det Node> getNodes(@Det ConditionalBlockImpl this) {
        @SuppressWarnings("determinism") // inference problem: Collections.emptyList()
        @Det List<@Det Node> result = Collections.emptyList();
        return result;
    }

    @Override
    public @Det @Nullable Node getLastNode(@Det ConditionalBlockImpl this) {
        return null;
    }

    @Override
    public @Det String toString(@Det ConditionalBlockImpl this) {
        return "ConditionalBlock()";
    }
}
