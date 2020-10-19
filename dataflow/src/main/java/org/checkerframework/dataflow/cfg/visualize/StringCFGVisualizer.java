package org.checkerframework.dataflow.cfg.visualize;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.RequiresDetToString;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.ControlFlowGraph;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.dataflow.cfg.block.ConditionalBlock;
import org.checkerframework.dataflow.cfg.block.SpecialBlock;
import org.checkerframework.dataflow.cfg.visualize.AbstractCFGVisualizer.VisualizeWhere;
import org.checkerframework.dataflow.expression.ArrayAccess;
import org.checkerframework.dataflow.expression.ClassName;
import org.checkerframework.dataflow.expression.FieldAccess;
import org.checkerframework.dataflow.expression.LocalVariable;
import org.checkerframework.dataflow.expression.MethodCall;

/** Generate the String representation of a control flow graph. */
public class StringCFGVisualizer<
                V extends AbstractValue<V>, S extends Store<S>, T extends TransferFunction<V, S>>
        extends AbstractCFGVisualizer<V, S, T> {

    @Override
    public @Det String getSeparator(@Det StringCFGVisualizer<V, S, T> this) {
        return "\n";
    }

    @Override
    public @OrderNonDet Map<String, Object> visualize(
            @Det StringCFGVisualizer<V, S, T> this,
            @Det ControlFlowGraph cfg,
            @Det Block entry,
            @Det @Nullable Analysis<V, S, T> analysis) {
        String stringGraph = visualizeGraph(cfg, entry, analysis);
        @OrderNonDet Map<String, Object> res = new HashMap<>();
        res.put("stringGraph", stringGraph);
        return res;
    }

    @SuppressWarnings("keyfor:enhancedfor.type.incompatible")
    @Override
    public @Det String visualizeNodes(
            @Det StringCFGVisualizer<V, S, T> this,
            @Det Set<@Det Block> blocks,
            @Det ControlFlowGraph cfg,
            @Det @Nullable Analysis<V, S, T> analysis) {
        StringJoiner sjStringNodes = new @Det StringJoiner(lineSeparator);
        IdentityHashMap<@Det Block, @Det List<Integer>> processOrder = getProcessOrder(cfg);

        // Generate all the Nodes.
        for (@KeyFor("processOrder") Block v : blocks) {
            sjStringNodes.add(v.getUid() + ":");
            if (verbose) {
                sjStringNodes.add(getProcessOrderSimpleString(processOrder.get(v)));
            }
            sjStringNodes.add(visualizeBlock(v, analysis));
            sjStringNodes.add("");
        }

        return sjStringNodes.toString().trim();
    }

    @Override
    protected @Det String visualizeEdge(
            @Det StringCFGVisualizer<V, S, T> this,
            @Det Object sId,
            @Det Object eId,
            @Det String flowRule) {
        if (this.verbose) {
            return sId + " -> " + eId + " " + flowRule;
        }
        return sId + " -> " + eId;
    }

    @Override
    public @Det String visualizeBlock(
            @Det StringCFGVisualizer<V, S, T> this,
            @Det Block bb,
            @Det @Nullable Analysis<V, S, T> analysis) {
        return super.visualizeBlockHelper(bb, analysis, lineSeparator).trim();
    }

    @Override
    public @Det String visualizeSpecialBlock(
            @Det StringCFGVisualizer<V, S, T> this, @Det SpecialBlock sbb) {
        return super.visualizeSpecialBlockHelper(sbb);
    }

    @Override
    public @Det String visualizeConditionalBlock(
            @Det StringCFGVisualizer<V, S, T> this, @Det ConditionalBlock cbb) {
        return "ConditionalBlock: then: "
                + cbb.getThenSuccessor().getUid()
                + ", else: "
                + cbb.getElseSuccessor().getUid();
    }

    @Override
    public @Det String visualizeBlockTransferInputBefore(
            @Det StringCFGVisualizer<V, S, T> this,
            @Det Block bb,
            @Det Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputHelper(
                VisualizeWhere.BEFORE, bb, analysis, lineSeparator);
    }

    @Override
    public @Det String visualizeBlockTransferInputAfter(
            @Det StringCFGVisualizer<V, S, T> this,
            @Det Block bb,
            @Det Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputHelper(
                VisualizeWhere.AFTER, bb, analysis, lineSeparator);
    }

    @Override
    @RequiresDetToString
    @SuppressWarnings("determinism") // toString is @Det because of @RequiresDetToString
    protected @Det String format(@Det StringCFGVisualizer<V, S, T> this, @Det Object obj) {
        return obj.toString();
    }

    @Override
    public @Det String visualizeStoreThisVal(@Det StringCFGVisualizer<V, S, T> this, @Det V value) {
        return storeEntryIndent + "this > " + value;
    }

    @Override
    public @Det String visualizeStoreLocalVar(
            @Det StringCFGVisualizer<V, S, T> this, @Det LocalVariable localVar, @Det V value) {
        return storeEntryIndent + localVar + " > " + value;
    }

    @Override
    public @Det String visualizeStoreFieldVal(
            @Det StringCFGVisualizer<V, S, T> this, @Det FieldAccess fieldAccess, @Det V value) {
        return storeEntryIndent + fieldAccess + " > " + value;
    }

    @Override
    public @Det String visualizeStoreArrayVal(
            @Det StringCFGVisualizer<V, S, T> this, @Det ArrayAccess arrayValue, @Det V value) {
        return storeEntryIndent + arrayValue + " > " + value;
    }

    @Override
    public @Det String visualizeStoreMethodVals(
            @Det StringCFGVisualizer<V, S, T> this, @Det MethodCall methodCall, @Det V value) {
        return storeEntryIndent + methodCall + " > " + value;
    }

    @Override
    public @Det String visualizeStoreClassVals(
            @Det StringCFGVisualizer<V, S, T> this, @Det ClassName className, @Det V value) {
        return storeEntryIndent + className + " > " + value;
    }

    @Override
    public @Det String visualizeStoreKeyVal(
            @Det StringCFGVisualizer<V, S, T> this, @Det String keyName, @Det Object value) {
        return storeEntryIndent + keyName + " = " + value;
    }

    /**
     * {@inheritDoc}
     *
     * <p>StringCFGVisualizer does not write into file, so left intentionally blank.
     */
    @Override
    public void shutdown() {}

    /**
     * {@inheritDoc}
     *
     * <p>StringCFGVisualizer does not need a specific header, so just return an empty string.
     */
    @Override
    protected String visualizeGraphHeader(@Det StringCFGVisualizer<V, S, T> this) {
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * <p>StringCFGVisualizer does not need a specific footer, so just return an empty string.
     */
    @Override
    protected String visualizeGraphFooter(@Det StringCFGVisualizer<V, S, T> this) {
        return "";
    }
}
