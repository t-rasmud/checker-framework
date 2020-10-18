package org.checkerframework.dataflow.cfg.visualize;

import java.util.Map;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.analysis.Analysis;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.analysis.TransferFunction;
import org.checkerframework.dataflow.cfg.ControlFlowGraph;
import org.checkerframework.dataflow.cfg.block.Block;
import org.checkerframework.dataflow.cfg.block.ConditionalBlock;
import org.checkerframework.dataflow.cfg.block.SpecialBlock;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.expression.ArrayAccess;
import org.checkerframework.dataflow.expression.ClassName;
import org.checkerframework.dataflow.expression.FieldAccess;
import org.checkerframework.dataflow.expression.LocalVariable;
import org.checkerframework.dataflow.expression.MethodCall;

/**
 * Perform some visualization on a control flow graph. The particular operations depend on the
 * implementation.
 *
 * @param <V> the abstract value type to be tracked by the analysis
 * @param <S> the store type used in the analysis
 * @param <T> the transfer function type that is used to approximate runtime behavior
 */
public interface CFGVisualizer<
        V extends AbstractValue<V>, S extends Store<S>, T extends TransferFunction<V, S>> {
    /**
     * Initialization method guaranteed to be called once before the first invocation of {@link
     * #visualize}.
     *
     * @param args implementation-dependent options
     */
    void init(@OrderNonDet Map<String, Object> args);

    /**
     * Returns the separator for lines within a node's representation.
     *
     * @return the separator for lines within a node's representation
     */
    public abstract @PolyDet String getSeparator(@PolyDet CFGVisualizer<V, S, T> this);

    /**
     * Output a visualization representing the control flow graph starting at {@code entry}. The
     * concrete actions are implementation dependent.
     *
     * <p>An invocation {@code visualize(cfg, entry, null);} does not output stores at the beginning
     * of basic blocks.
     *
     * @param cfg the CFG to visualize
     * @param entry the entry node of the control flow graph to be represented
     * @param analysis an analysis containing information about the program represented by the CFG.
     *     The information includes {@link Store}s that are valid at the beginning of basic blocks
     *     reachable from {@code entry} and per-node information for value producing {@link Node}s.
     *     Can also be {@code null} to indicate that this information should not be output.
     * @return visualization results, e.g. generated file names ({@link DOTCFGVisualizer}) or a
     *     String representation of the CFG ({@link StringCFGVisualizer})
     */
    @Nullable @OrderNonDet Map<String, Object> visualize(
            @PolyDet CFGVisualizer<V, S, T> this,
            ControlFlowGraph cfg,
            Block entry,
            @Nullable Analysis<V, S, T> analysis);

    /**
     * Delegate the visualization responsibility to the passed {@link Store} instance, which will
     * call back to this visualizer instance for sub-components.
     *
     * @param store the store to visualize
     * @return the String representation of the given store
     */
    @PolyDet String visualizeStore(@PolyDet CFGVisualizer<V, S, T> this, @PolyDet S store);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize a local variable.
     *
     * @param localVar the local variable
     * @param value the value of the local variable
     * @return the String representation of the local variable
     */
    @PolyDet String visualizeStoreLocalVar(
            @PolyDet CFGVisualizer<V, S, T> this,
            @PolyDet LocalVariable localVar,
            @PolyDet V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of the current
     * object {@code this} in this Store.
     *
     * @param value the value of the current object {@code this}
     * @return the String representation of {@code this}
     */
    @PolyDet String visualizeStoreThisVal(@PolyDet CFGVisualizer<V, S, T> this, @PolyDet V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of one field
     * collected by this Store.
     *
     * @param fieldAccess the field
     * @param value the value of the field
     * @return the String representation of the field
     */
    @PolyDet String visualizeStoreFieldVal(
            @PolyDet CFGVisualizer<V, S, T> this,
            @PolyDet FieldAccess fieldAccess,
            @PolyDet V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of one array
     * collected by this Store.
     *
     * @param arrayValue the array
     * @param value the value of the array
     * @return the String representation of the array
     */
    @PolyDet String visualizeStoreArrayVal(
            @PolyDet CFGVisualizer<V, S, T> this, @PolyDet ArrayAccess arrayValue, V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of pure method
     * calls collected by this Store.
     *
     * @param methodCall the pure method call
     * @param value the value of the pure method call
     * @return the String representation of the pure method call
     */
    @PolyDet String visualizeStoreMethodVals(
            @PolyDet CFGVisualizer<V, S, T> this, MethodCall methodCall, @PolyDet V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of class names
     * collected by this Store.
     *
     * @param className the class name
     * @param value the value of the class name
     * @return the String representation of the class name
     */
    @PolyDet String visualizeStoreClassVals(
            @PolyDet CFGVisualizer<V, S, T> this, @PolyDet ClassName className, @PolyDet V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the specific information
     * collected according to the specific kind of Store. Currently, these Stores call this method:
     * {@code LockStore}, {@code NullnessStore}, and {@code InitializationStore} to visualize
     * additional information.
     *
     * @param keyName the name of the specific information to be visualized
     * @param value the value of the specific information to be visualized
     * @return the String representation of the specific information
     */
    @PolyDet String visualizeStoreKeyVal(
            @PolyDet CFGVisualizer<V, S, T> this, @PolyDet String keyName, @PolyDet Object value);

    /**
     * Visualize a block based on the analysis.
     *
     * @param bb the block
     * @param analysis the current analysis
     * @return the String representation of the given block
     */
    @PolyDet String visualizeBlock(
            @PolyDet CFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet @Nullable Analysis<V, S, T> analysis);

    /**
     * Visualize a SpecialBlock.
     *
     * @param sbb the special block
     * @return the String representation of the type of the special block {@code sbb}: entry, exit,
     *     or exceptional-exit
     */
    @PolyDet String visualizeSpecialBlock(@PolyDet CFGVisualizer<V, S, T> this, @PolyDet SpecialBlock sbb);

    /**
     * Visualize a ConditionalBlock.
     *
     * @param cbb the conditional block
     * @return the String representation of the conditional block
     */
    @PolyDet String visualizeConditionalBlock(
            @PolyDet CFGVisualizer<V, S, T> this, @PolyDet ConditionalBlock cbb);

    /**
     * Visualize the transferInput before a Block based on the analysis.
     *
     * @param bb the block
     * @param analysis the current analysis
     * @return the String representation of the transferInput before the given block
     */
    @PolyDet String visualizeBlockTransferInputBefore(
            @PolyDet CFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet Analysis<V, S, T> analysis);

    /**
     * Visualize the transferInput after a Block based on the analysis.
     *
     * @param bb the block
     * @param analysis the current analysis
     * @return the String representation of the transferInput after the given block
     */
    @PolyDet String visualizeBlockTransferInputAfter(
            @PolyDet CFGVisualizer<V, S, T> this,
            @PolyDet Block bb,
            @PolyDet Analysis<V, S, T> analysis);

    /**
     * Visualize a Node based on the analysis.
     *
     * @param t the node
     * @param analysis the current analysis
     * @return the String representation of the given node
     */
    @PolyDet String visualizeBlockNode(
            @PolyDet CFGVisualizer<V, S, T> this,
            @PolyDet Node t,
            @PolyDet @Nullable Analysis<V, S, T> analysis);

    /** Shutdown method called once from the shutdown hook of the {@code BaseTypeChecker}. */
    void shutdown();
}
