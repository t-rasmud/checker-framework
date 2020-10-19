package org.checkerframework.dataflow.cfg.visualize;

import java.util.Map;
import org.checkerframework.checker.determinism.qual.*;
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
    public abstract @Det String getSeparator(@Det CFGVisualizer<V, S, T> this);

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
            @Det CFGVisualizer<V, S, T> this,
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
    @Det String visualizeStore(@Det CFGVisualizer<V, S, T> this, @Det S store);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize a local variable.
     *
     * @param localVar the local variable
     * @param value the value of the local variable
     * @return the String representation of the local variable
     */
    @Det String visualizeStoreLocalVar(
            @Det CFGVisualizer<V, S, T> this, @Det LocalVariable localVar, @Det V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of the current
     * object {@code this} in this Store.
     *
     * @param value the value of the current object {@code this}
     * @return the String representation of {@code this}
     */
    @Det String visualizeStoreThisVal(@Det CFGVisualizer<V, S, T> this, @Det V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of one field
     * collected by this Store.
     *
     * @param fieldAccess the field
     * @param value the value of the field
     * @return the String representation of the field
     */
    @Det String visualizeStoreFieldVal(
            @Det CFGVisualizer<V, S, T> this, @Det FieldAccess fieldAccess, @Det V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of one array
     * collected by this Store.
     *
     * @param arrayValue the array
     * @param value the value of the array
     * @return the String representation of the array
     */
    @Det String visualizeStoreArrayVal(
            @Det CFGVisualizer<V, S, T> this, @Det ArrayAccess arrayValue, V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of pure method
     * calls collected by this Store.
     *
     * @param methodCall the pure method call
     * @param value the value of the pure method call
     * @return the String representation of the pure method call
     */
    @Det String visualizeStoreMethodVals(
            @Det CFGVisualizer<V, S, T> this, MethodCall methodCall, @Det V value);

    /**
     * Called by {@code CFAbstractStore#internalVisualize()} to visualize the value of class names
     * collected by this Store.
     *
     * @param className the class name
     * @param value the value of the class name
     * @return the String representation of the class name
     */
    @Det String visualizeStoreClassVals(
            @Det CFGVisualizer<V, S, T> this, @Det ClassName className, @Det V value);

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
    @Det String visualizeStoreKeyVal(
            @Det CFGVisualizer<V, S, T> this, @Det String keyName, @Det Object value);

    /**
     * Visualize a block based on the analysis.
     *
     * @param bb the block
     * @param analysis the current analysis
     * @return the String representation of the given block
     */
    @Det String visualizeBlock(
            @Det CFGVisualizer<V, S, T> this,
            @Det Block bb,
            @Det @Nullable Analysis<V, S, T> analysis);

    /**
     * Visualize a SpecialBlock.
     *
     * @param sbb the special block
     * @return the String representation of the type of the special block {@code sbb}: entry, exit,
     *     or exceptional-exit
     */
    @Det String visualizeSpecialBlock(@Det CFGVisualizer<V, S, T> this, @Det SpecialBlock sbb);

    /**
     * Visualize a ConditionalBlock.
     *
     * @param cbb the conditional block
     * @return the String representation of the conditional block
     */
    @Det String visualizeConditionalBlock(@Det CFGVisualizer<V, S, T> this, @Det ConditionalBlock cbb);

    /**
     * Visualize the transferInput before a Block based on the analysis.
     *
     * @param bb the block
     * @param analysis the current analysis
     * @return the String representation of the transferInput before the given block
     */
    @Det String visualizeBlockTransferInputBefore(
            @Det CFGVisualizer<V, S, T> this, @Det Block bb, @Det Analysis<V, S, T> analysis);

    /**
     * Visualize the transferInput after a Block based on the analysis.
     *
     * @param bb the block
     * @param analysis the current analysis
     * @return the String representation of the transferInput after the given block
     */
    @Det String visualizeBlockTransferInputAfter(
            @Det CFGVisualizer<V, S, T> this, @Det Block bb, @Det Analysis<V, S, T> analysis);

    /**
     * Visualize a Node based on the analysis.
     *
     * @param t the node
     * @param analysis the current analysis
     * @return the String representation of the given node
     */
    @Det String visualizeBlockNode(
            @Det CFGVisualizer<V, S, T> this,
            @Det Node t,
            @Det @Nullable Analysis<V, S, T> analysis);

    /** Shutdown method called once from the shutdown hook of the {@code BaseTypeChecker}. */
    void shutdown();
}
