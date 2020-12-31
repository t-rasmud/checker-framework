package org.checkerframework.dataflow.livevariable;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.cfg.node.BinaryOperationNode;
import org.checkerframework.dataflow.cfg.node.FieldAccessNode;
import org.checkerframework.dataflow.cfg.node.InstanceOfNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.cfg.node.TernaryExpressionNode;
import org.checkerframework.dataflow.cfg.node.TypeCastNode;
import org.checkerframework.dataflow.cfg.node.UnaryOperationNode;
import org.checkerframework.dataflow.cfg.visualize.CFGVisualizer;
import org.checkerframework.dataflow.expression.Receiver;
import org.checkerframework.javacutil.BugInCF;

/** A live variable store contains a set of live variables represented by nodes. */
public class LiveVarStore implements Store<LiveVarStore> {

    /** A set of live variable abstract values. */
    private final Set<LiveVarValue> liveVarValueSet;

    /** Create a new LiveVarStore. */
    // true positive; `liveVarValueSet` is declared as a Det Set but assigned an OrderNonDet HashSet
    // Fixed: https://github.com/typetools/checker-framework/commit/18f22f83efcb31791927adfc4f01d064b8eb8523
    public LiveVarStore() {
        liveVarValueSet = new HashSet<>();
    }

    /**
     * Create a new LiveVarStore.
     *
     * @param liveVarValueSet a set of live variable abstract values
     */
    public LiveVarStore(Set<LiveVarValue> liveVarValueSet) {
        this.liveVarValueSet = liveVarValueSet;
    }

    /**
     * Add the information of a live variable into the live variable set.
     *
     * @param variable a live variable
     */
    public void putLiveVar(LiveVarValue variable) {
        liveVarValueSet.add(variable);
    }

    /**
     * Remove the information of a live variable from the live variable set.
     *
     * @param variable a live variable
     */
    public void killLiveVar(LiveVarValue variable) {
        liveVarValueSet.remove(variable);
    }

    /**
     * Add the information of live variables in an expression to the live variable set.
     *
     * @param expression a node
     */
    public void addUseInExpression(Node expression) {
        // TODO Do we need a AbstractNodeScanner to do the following job?
        if (expression instanceof LocalVariableNode || expression instanceof FieldAccessNode) {
            LiveVarValue liveVarValue = new LiveVarValue(expression);
            putLiveVar(liveVarValue);
        } else if (expression instanceof UnaryOperationNode) {
            UnaryOperationNode unaryNode = (UnaryOperationNode) expression;
            addUseInExpression(unaryNode.getOperand());
        } else if (expression instanceof TernaryExpressionNode) {
            TernaryExpressionNode ternaryNode = (TernaryExpressionNode) expression;
            addUseInExpression(ternaryNode.getConditionOperand());
            addUseInExpression(ternaryNode.getThenOperand());
            addUseInExpression(ternaryNode.getElseOperand());
        } else if (expression instanceof TypeCastNode) {
            TypeCastNode typeCastNode = (TypeCastNode) expression;
            addUseInExpression(typeCastNode.getOperand());
        } else if (expression instanceof InstanceOfNode) {
            InstanceOfNode instanceOfNode = (InstanceOfNode) expression;
            addUseInExpression(instanceOfNode.getOperand());
        } else if (expression instanceof BinaryOperationNode) {
            BinaryOperationNode binaryNode = (BinaryOperationNode) expression;
            addUseInExpression(binaryNode.getLeftOperand());
            addUseInExpression(binaryNode.getRightOperand());
        }
    }

    @Override
    @SuppressWarnings("determinism:return.type.incompatible") // false positive, I think
    public @PolyDet boolean equals(@PolyDet LiveVarStore this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof LiveVarStore)) {
            return false;
        }
        LiveVarStore other = (LiveVarStore) obj;
        return other.liveVarValueSet.equals(this.liveVarValueSet);
    }

    @Override
    public @NonDet int hashCode(@PolyDet LiveVarStore this) {
        return this.liveVarValueSet.hashCode();
    }

    @Override
    @SuppressWarnings(
            "determinism") // valid rule relaxation: copy clearly preserved determinism type
    public LiveVarStore copy() {
        return new LiveVarStore(new HashSet<>(liveVarValueSet));
    }

    @Override
    @SuppressWarnings(
            "determinism") // imprecise field access rule: accessing @OrderNonDet field of @PolyDet
    // receiver gives @NonDet, should be @PolyDet("upDet")
    public @PolyDet LiveVarStore leastUpperBound(
            @PolyDet LiveVarStore this, @PolyDet LiveVarStore other) {
        Set<@Det LiveVarValue> liveVarValueSetLub = new HashSet<>();
        liveVarValueSetLub.addAll(this.liveVarValueSet);
        liveVarValueSetLub.addAll(other.liveVarValueSet);
        return new LiveVarStore(liveVarValueSetLub);
    }

    /** It should not be called since it is not used by the backward analysis. */
    @Override
    public LiveVarStore widenedUpperBound(LiveVarStore previous) {
        throw new BugInCF("wub of LiveVarStore get called!");
    }

    @Override
    public boolean canAlias(Receiver a, Receiver b) {
        return true;
    }

    @Override
    public String visualize(CFGVisualizer<?, LiveVarStore, ?> viz) {
        String key = "live variables";
        if (liveVarValueSet.isEmpty()) {
            return viz.visualizeStoreKeyVal(key, "none");
        }
        StringJoiner sjStoreVal = new StringJoiner(", ");
        for (LiveVarValue liveVarValue : liveVarValueSet) {
            sjStoreVal.add(liveVarValue.toString());
        }
        return viz.visualizeStoreKeyVal(key, sjStoreVal.toString());
    }

    @Override
    @SuppressWarnings("determinism") // toString only on @Det
    public String toString() {
        return liveVarValueSet.toString();
    }
}
