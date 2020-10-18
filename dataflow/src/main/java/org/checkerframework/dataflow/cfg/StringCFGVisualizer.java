package org.checkerframework.dataflow.cfg;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.determinism.qual.RequiresDetToString;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.AbstractCFGVisualizer.VisualizeWhere;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.dataflow.cfg.block.ConditionalBlock;
import org.checkerframework.dataflow.cfg.block.SpecialBlock;
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
    public @PolyDet String getSeparator(@PolyDet StringCFGVisualizer<V, S, T> this) {
        return "\n";
    }

    @Override
    public @OrderNonDet Map<String, Object> visualize(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet ControlFlowGraph cfg,
            @PolyDet Block entry,
            @PolyDet @Nullable Analysis<V, S, T> analysis) {
        String stringGraph = visualizeGraph(cfg, entry, analysis);
        Map<@Det String, @NonDet Object> res = new HashMap<>();
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
    public @PolyDet String visualizeNodes(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet Set<@PolyDet Block> blocks,
            @PolyDet ControlFlowGraph cfg,
            @PolyDet @Nullable Analysis<V, S, T> analysis) {
        StringJoiner sjStringNodes = new @PolyDet StringJoiner(lineSeparator);
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
    protected @PolyDet String visualizeEdge(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet Object sId,
            @PolyDet Object eId,
            @PolyDet String flowRule) {
        if (this.verbose) {
            return sId + " -> " + eId + " " + flowRule;
        }
        return sId + " -> " + eId;
    }

    @Override
    public @PolyDet String visualizeBlock(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet @Nullable Analysis<V, S, T> analysis) {
        return super.visualizeBlockHelper(bb, analysis, lineSeparator).trim();
    }

    @Override
    public @PolyDet String visualizeSpecialBlock(
            @PolyDet StringCFGVisualizer<V, S, T> this, @PolyDet SpecialBlock sbb) {
        return super.visualizeSpecialBlockHelper(sbb);
    }

    @Override
    public @PolyDet String visualizeConditionalBlock(
            @PolyDet StringCFGVisualizer<V, S, T> this, @PolyDet ConditionalBlock cbb) {
        return "ConditionalBlock: then: "
                + cbb.getThenSuccessor().getUid()
                + ", else: "
                + cbb.getElseSuccessor().getUid();
    }

    @Override
    public @PolyDet String visualizeBlockTransferInputBefore(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputHelper(
                VisualizeWhere.BEFORE, bb, analysis, lineSeparator);
    }

    @Override
    public @PolyDet String visualizeBlockTransferInputAfter(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet Analysis<V, S, T> analysis) {
        return super.visualizeBlockTransferInputHelper(
                VisualizeWhere.AFTER, bb, analysis, lineSeparator);
    }

    @Override
    @RequiresDetToString
    @SuppressWarnings("determinism") // toString is @Det because of @RequiresDetToString
    protected @PolyDet String format(
            @PolyDet StringCFGVisualizer<V, S, T> this, @PolyDet Object obj) {
        return obj.toString();
    }

    @Override
    public @PolyDet String visualizeStoreThisVal(
            @PolyDet StringCFGVisualizer<V, S, T> this, @PolyDet V value) {
        return storeEntryIndent + "this > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreLocalVar(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet LocalVariable localVar,
            @PolyDet V value) {
        return storeEntryIndent + localVar + " > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreFieldVal(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet FieldAccess fieldAccess,
            @PolyDet V value) {
        return storeEntryIndent + fieldAccess + " > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreArrayVal(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet ArrayAccess arrayValue,
            @PolyDet V value) {
        return storeEntryIndent + arrayValue + " > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreMethodVals(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet MethodCall methodCall,
            @PolyDet V value) {
        return storeEntryIndent + methodCall + " > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreClassVals(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet ClassName className,
            @PolyDet V value) {
        return storeEntryIndent + className + " > " + value;
    }

    @Override
    public @PolyDet String visualizeStoreKeyVal(
            @PolyDet StringCFGVisualizer<V, S, T> this,
            @PolyDet String keyName,
            @PolyDet Object value) {
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
    protected String visualizeGraphHeader(@PolyDet StringCFGVisualizer<V, S, T> this) {
        return "";
    }

    /**
     * {@inheritDoc}
     *
     * <p>StringCFGVisualizer does not need a specific footer, so just return an empty string.
     */
    @Override
    protected String visualizeGraphFooter(@PolyDet StringCFGVisualizer<V, S, T> this) {
        return "";
    }
}
