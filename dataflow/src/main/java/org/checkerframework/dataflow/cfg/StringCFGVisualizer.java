package org.checkerframework.dataflow.cfg;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.RequiresDetToString;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.FlowExpressions;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.dataflow.cfg.block.ConditionalBlock;
import org.checkerframework.dataflow.cfg.block.SpecialBlock;

/** Generate the String representation of a control flow graph. */
public class StringCFGVisualizer<
                V extends AbstractValue<V>, S extends Store<S>, T extends TransferFunction<V, S>>
        extends AbstractCFGVisualizer<V, S, T> {

    @Override
    public @NonDet Map<String, @NonDet Object> visualize(
            ControlFlowGraph cfg, Block entry, @Nullable Analysis<V, S, T> analysis) {
        String stringGraph = visualizeGraph(cfg, entry, analysis);
        @NonDet Map<@Det String, @NonDet Object> res = new @NonDet HashMap<>();
        @SuppressWarnings({
            "determinism", // valid rule relaxation: no aliasing, so valid to mutate @NonDet
            // collection
            "UnusedVariable"
        })
        Object ignore = res.put("stringGraph", stringGraph);
        return res;
    }

    @SuppressWarnings("keyfor:enhancedfor.type.incompatible")
    @Override
    public @NonDet String visualizeNodes(
            Set<Block> blocks, ControlFlowGraph cfg, @Nullable Analysis<V, S, T> analysis) {
        StringJoiner sjStringNodes = new @NonDet StringJoiner(lineSeparator, lineSeparator, "");
        IdentityHashMap<@Det Block, @Det List<Integer>> processOrder = getProcessOrder(cfg);

        // Generate all the Nodes.
        for (@KeyFor("processOrder") Block v : blocks) {
            sjStringNodes.add(v.getId() + ":");
            if (verbose) {
                sjStringNodes.add(getProcessOrderSimpleString(processOrder.get(v)));
            }
            sjStringNodes.add(visualizeBlock(v, analysis));
        }

        // Remove the line separator from the end of the string.
        String stringNodes = sjStringNodes.toString();
        if (stringNodes.endsWith(lineSeparator)) {
            stringNodes = stringNodes.substring(0, stringNodes.length() - lineSeparator.length());
        }

        return stringNodes;
    }

    @Override
    protected String addEdge(Object sId, Object eId, String flowRule) {
        if (this.verbose) {
            return sId + " -> " + eId + " " + flowRule + lineSeparator;
        }
        return sId + " -> " + eId + lineSeparator;
    }

    @Override
    public @NonDet String visualizeBlock(Block bb, @Nullable Analysis<V, S, T> analysis) {
        return super.visualizeBlockHelper(bb, analysis, lineSeparator);
    }

    @Override
    public String visualizeSpecialBlock(SpecialBlock sbb) {
        return super.visualizeSpecialBlockHelper(sbb, lineSeparator);
    }

    @Override
    public String visualizeConditionalBlock(ConditionalBlock cbb) {
        return "ConditionalBlock: then: "
                + cbb.getThenSuccessor().getId()
                + ", else: "
                + cbb.getElseSuccessor().getId()
                + lineSeparator;
    }

    @Override
    public @NonDet String visualizeBlockTransferInputBefore(Block bb, Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputBeforeHelper(bb, analysis, lineSeparator);
    }

    @Override
    public @NonDet String visualizeBlockTransferInputAfter(Block bb, Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputAfterHelper(bb, analysis, lineSeparator);
    }

    @Override
    @RequiresDetToString
    @SuppressWarnings("determinism") // toString is @Det because of @RequiresDetToString
    protected String format(Object obj) {
        return obj.toString();
    }

    @Override
    public String visualizeStoreThisVal(V value) {
        return storeEntryIndent + "this > " + value + lineSeparator;
    }

    @Override
    public String visualizeStoreLocalVar(FlowExpressions.LocalVariable localVar, V value) {
        return storeEntryIndent + localVar + " > " + value + lineSeparator;
    }

    @Override
    public String visualizeStoreFieldVals(FlowExpressions.FieldAccess fieldAccess, V value) {
        return storeEntryIndent + fieldAccess + " > " + value + lineSeparator;
    }

    @Override
    public String visualizeStoreArrayVal(FlowExpressions.ArrayAccess arrayValue, V value) {
        return storeEntryIndent + arrayValue + " > " + value + lineSeparator;
    }

    @Override
    public String visualizeStoreMethodVals(FlowExpressions.MethodCall methodCall, V value) {
        return storeEntryIndent + methodCall + " > " + value + lineSeparator;
    }

    @Override
    public String visualizeStoreClassVals(FlowExpressions.ClassName className, V value) {
        return storeEntryIndent + className + " > " + value + lineSeparator;
    }

    @Override
    public @NonDet String visualizeStoreKeyVal(String keyName, @NonDet Object value) {
        return storeEntryIndent + keyName + " = " + value + lineSeparator;
    }

    @Override
    public String visualizeStoreHeader(String classCanonicalName) {
        return classCanonicalName + "(" + lineSeparator;
    }

    @Override
    public String visualizeStoreFooter() {
        return ")" + lineSeparator;
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
    protected String visualizeGraphHeader() {
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * <p>StringCFGVisualizer does not need a specific footer, so just return an empty string.
     */
    @Override
    protected String visualizeGraphFooter() {
        return "";
    }
}
