package org.checkerframework.dataflow.livevariable;

import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.javacutil.BugInCF;

/** A live variable (which is represented by a node) wrapper turning node into abstract value. */
public class LiveVarValue implements AbstractValue<LiveVarValue> {

    /**
     * A live variable is represented by a node, which can be a {@link
     * org.checkerframework.dataflow.cfg.node.LocalVariableNode} or {@link
     * org.checkerframework.dataflow.cfg.node.FieldAccessNode}.
     */
    protected final Node liveVariable;

    @Override
    public LiveVarValue leastUpperBound(LiveVarValue other) {
        throw new BugInCF("lub of LiveVar get called!");
    }

    /**
     * Create a new live variable.
     *
     * @param n a node
     */
    public LiveVarValue(Node n) {
        this.liveVariable = n;
    }

    @Override
    public @NonDet int hashCode(@PolyDet LiveVarValue this) {
        return this.liveVariable.hashCode();
    }

    @Override
    public @PolyDet boolean equals(@PolyDet LiveVarValue this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof LiveVarValue)) {
            return false;
        }
        LiveVarValue other = (LiveVarValue) obj;
        return this.liveVariable.equals(other.liveVariable);
    }

    @Override
    public @PolyDet String toString(@PolyDet LiveVarValue this) {
        return this.liveVariable.toString();
    }
}
