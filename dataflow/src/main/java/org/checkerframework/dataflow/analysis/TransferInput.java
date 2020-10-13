package org.checkerframework.dataflow.analysis;

import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.cfg.node.Node;
import org.plumelib.util.UniqueId;
import org.plumelib.util.UtilPlume;

/**
 * {@code TransferInput} is used as the input type of the individual transfer functions of a {@link
 * ForwardTransferFunction} or a {@link BackwardTransferFunction}. It also contains a reference to
 * the node for which the transfer function will be applied.
 *
 * <p>A {@code TransferInput} contains one or two stores. If two stores are present, one belongs to
 * 'then', and the other to 'else'.
 *
 * @param <V> type of the abstract value that is tracked
 * @param <S> the store type used in the analysis
 */
public class TransferInput<V extends AbstractValue<V>, S extends Store<S>> implements UniqueId {

    /** The corresponding node. */
    // TODO: explain when the node is changed.
    protected @Nullable Node node;

    /**
     * The regular result store (or {@code null} if none is present, because {@link #thenStore} and
     * {@link #elseStore} are set). The following invariant is maintained:
     *
     * <pre><code>
     * store == null &hArr; thenStore != null &amp;&amp; elseStore != null
     * </code></pre>
     */
    protected final @Nullable S store;

    /**
     * The 'then' result store (or {@code null} if none is present). See invariant at {@link
     * #store}.
     */
    protected final @Nullable S thenStore;

    /**
     * The 'else' result store (or {@code null} if none is present). See invariant at {@link
     * #store}.
     */
    protected final @Nullable S elseStore;

    /** The corresponding analysis class to get intermediate flow results. */
    protected final Analysis<V, S, ?> analysis;

    /** The unique ID of this object. */
    final transient long uid = UniqueId.nextUid.getAndIncrement();

    @Override
    public long getUid() {
        return uid;
    }

    /**
     * Create a {@link TransferInput}, given a {@link TransferResult} and a node-value mapping.
     *
     * <p><em>Aliasing</em>: The stores returned by any methods of {@code to} will be stored
     * internally and are not allowed to be used elsewhere. Full control of them is transferred to
     * this object.
     *
     * <p>The node-value mapping {@code nodeValues} is provided by the analysis and is only read
     * from within this {@link TransferInput}.
     *
     * @param n {@link #node}
     * @param analysis {@link #analysis}
     * @param to a transfer result
     */
    public TransferInput(Node n, Analysis<V, S, ?> analysis, TransferResult<V, S> to) {
        node = n;
        this.analysis = analysis;
        if (to.containsTwoStores()) {
            thenStore = to.getThenStore();
            elseStore = to.getElseStore();
            store = null;
        } else {
            store = to.getRegularStore();
            thenStore = elseStore = null;
        }
    }

    /**
     * Create a {@link TransferInput}, given a store and a node-value mapping.
     *
     * <p><em>Aliasing</em>: The store {@code s} will be stored internally and is not allowed to be
     * used elsewhere. Full control over {@code s} is transferred to this object.
     *
     * <p>The node-value mapping {@code nodeValues} is provided by the analysis and is only read
     * from within this {@link TransferInput}.
     *
     * @param n {@link #node}
     * @param analysis {@link #analysis}
     * @param s {@link #store}
     */
    public TransferInput(@Nullable Node n, Analysis<V, S, ?> analysis, S s) {
        node = n;
        this.analysis = analysis;
        store = s;
        thenStore = elseStore = null;
    }

    /**
     * Create a {@link TransferInput}, given two stores and a node-value mapping.
     *
     * <p><em>Aliasing</em>: The two stores {@code s1} and {@code s2} will be stored internally and
     * are not allowed to be used elsewhere. Full control of them is transferred to this object.
     *
     * @param n a node
     * @param analysis {@link #analysis}
     * @param s1 {@link #thenStore}
     * @param s2 {@link #elseStore}
     */
    public TransferInput(@Nullable Node n, Analysis<V, S, ?> analysis, S s1, S s2) {
        node = n;
        this.analysis = analysis;
        thenStore = s1;
        elseStore = s2;
        store = null;
    }

    /**
     * Copy constructor.
     *
     * @param from a {@link TransferInput} to copy
     */
    protected TransferInput(TransferInput<V, S> from) {
        this.node = from.node;
        this.analysis = from.analysis;
        if (from.store == null) {
            assert from.thenStore != null && from.elseStore != null
                    : "@AssumeAssertion(nullness): invariant";
            thenStore = from.thenStore.copy();
            elseStore = from.elseStore.copy();
            store = null;
        } else {
            store = from.store.copy();
            thenStore = elseStore = null;
        }
    }

    /**
     * Returns the {@link Node} for this {@link TransferInput}.
     *
     * @return the {@link Node} for this {@link TransferInput}
     */
    public @Nullable Node getNode() {
        return node;
    }

    /**
     * Returns the abstract value of node {@code n}, which is required to be a 'sub-node' (that is,
     * a direct or indirect child) of the node this transfer input is associated with. Furthermore,
     * {@code n} cannot be a l-value node. Returns {@code null} if no value is available.
     *
     * @param n a node
     * @return the abstract value of node {@code n}, or {@code null} if no value is available
     */
    public @Nullable V getValueOfSubNode(Node n) {
        return analysis.getValue(n);
    }

    /**
     * Returns the regular result store produced if no exception is thrown by the {@link Node}
     * corresponding to this transfer function result.
     *
     * @return the regular result store produced if no exception is thrown by the {@link Node}
     *     corresponding to this transfer function result
     */
    public @PolyDet S getRegularStore(@PolyDet TransferInput<V, S> this) {
        if (store == null) {
            assert thenStore != null && elseStore != null : "@AssumeAssertion(nullness): invariant";
            return thenStore.leastUpperBound(elseStore);
        } else {
            return store;
        }
    }

    /**
     * Returns the result store produced if the {@link Node} this result belongs to evaluates to
     * {@code true}.
     *
     * @return the result store produced if the {@link Node} this result belongs to evaluates to
     *     {@code true}
     */
    public @PolyDet S getThenStore(@PolyDet TransferInput<V, S> this) {
        if (store == null) {
            assert thenStore != null : "@AssumeAssertion(nullness): invariant";
            return thenStore;
        }
        return store;
    }

    /**
     * Returns the result store produced if the {@link Node} this result belongs to evaluates to
     * {@code false}.
     *
     * @return the result store produced if the {@link Node} this result belongs to evaluates to
     *     {@code false}
     */
    public @PolyDet S getElseStore(@PolyDet TransferInput<V, S> this) {
        if (store == null) {
            assert elseStore != null : "@AssumeAssertion(nullness): invariant";
            return elseStore;
        }
        // copy the store such that it is the same as the result of getThenStore
        // (that is, identical according to equals), but two different objects.
        return store.copy();
    }

    /**
     * Returns {@code true} if and only if this transfer input contains two stores that are
     * potentially not equal. Note that the result {@code true} does not imply that {@code
     * getRegularStore} cannot be called (or vice versa for {@code false}). Rather, it indicates
     * that {@code getThenStore} or {@code getElseStore} can be used to give more precise results.
     * Otherwise, if the result is {@code false}, then all three methods {@code getRegularStore},
     * {@code getThenStore}, and {@code getElseStore} return equivalent stores.
     *
     * @return {@code true} if and only if this transfer input contains two stores that are
     *     potentially not equal
     */
    public @PolyDet boolean containsTwoStores(@PolyDet TransferInput<V, S> this) {
        return store == null;
    }

    /**
     * Returns an exact copy of this store.
     *
     * @return an exact copy of this store
     */
    public TransferInput<V, S> copy() {
        return new TransferInput<>(this);
    }

    /**
     * Compute the least upper bound of two stores.
     *
     * <p><em>Important</em>: This method must fulfill the same contract as {@code leastUpperBound}
     * of {@link Store}.
     *
     * @param other a transfer input
     * @return the least upper bound of this and {@code other}
     */
    public TransferInput<V, S> leastUpperBound(TransferInput<V, S> other) {
        if (store == null) {
            S newThenStore = getThenStore().leastUpperBound(other.getThenStore());
            S newElseStore = getElseStore().leastUpperBound(other.getElseStore());
            return new TransferInput<>(node, analysis, newThenStore, newElseStore);
        } else {
            if (other.store == null) {
                // make sure we do not lose precision and keep two stores if at
                // least one of the two TransferInput's has two stores.
                return other.leastUpperBound(this);
            }
            return new TransferInput<>(
                    node, analysis, store.leastUpperBound(other.getRegularStore()));
        }
    }

    @Override
    public @PolyDet boolean equals(@PolyDet TransferInput<V, S> this, @PolyDet @Nullable Object o) {
        if (o instanceof TransferInput) {
            @SuppressWarnings("unchecked")
            TransferInput<V, S> other = (TransferInput<V, S>) o;
            if (containsTwoStores()) {
                if (other.containsTwoStores()) {
                    return getThenStore().equals(other.getThenStore())
                            && getElseStore().equals(other.getElseStore());
                }
            } else {
                if (!other.containsTwoStores()) {
                    return getRegularStore().equals(other.getRegularStore());
                }
            }
        }
        return false;
    }

    @Override
    public @NonDet int hashCode(@PolyDet TransferInput<V, S> this) {
        return Objects.hash(this.analysis, this.node, this.store, this.thenStore, this.elseStore);
    }

    @Override
    public @PolyDet String toString(@PolyDet TransferInput<V, S> this) {
        if (store == null) {
            return "[then="
                    + UtilPlume.indentLinesExceptFirst(2, thenStore)
                    + ","
                    + System.lineSeparator()
                    + "  else="
                    + UtilPlume.indentLinesExceptFirst(2, elseStore)
                    + "]";
        } else {
            return "[" + store + "]";
        }
    }
}
