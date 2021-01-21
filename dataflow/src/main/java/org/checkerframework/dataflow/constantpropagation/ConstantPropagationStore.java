package org.checkerframework.dataflow.constantpropagation;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;
import org.checkerframework.dataflow.cfg.node.IntegerLiteralNode;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.cfg.visualize.CFGVisualizer;
import org.checkerframework.dataflow.expression.Receiver;

public class ConstantPropagationStore implements Store<ConstantPropagationStore> {

    /** Information about variables gathered so far. */
    Map<Node, Constant> contents;

    /** Creates a new ConstantPropagationStore. */
    public ConstantPropagationStore() {
        // true positive; `contents` is declared as a Det map but is assigned an OrderNonDet HashMap
        // Fixed: https://github.com/typetools/checker-framework/commit/0a0ea1021409ca8d2d5d7e7f57556ed45cd2472d
        contents = new HashMap<>();
    }

    protected ConstantPropagationStore(Map<Node, Constant> contents) {
        this.contents = contents;
    }

    public Constant getInformation(Node n) {
        if (contents.containsKey(n)) {
            return contents.get(n);
        }
        return new Constant(Constant.Type.TOP);
    }

    public void mergeInformation(Node n, Constant val) {
        Constant value;
        if (contents.containsKey(n)) {
            value = val.leastUpperBound(contents.get(n));
        } else {
            value = val;
        }
        // TODO: remove (only two nodes supported atm)
        assert n instanceof IntegerLiteralNode || n instanceof LocalVariableNode;
        contents.put(n, value);
    }

    public void setInformation(Node n, Constant val) {
        // TODO: remove (only two nodes supported atm)
        assert n instanceof IntegerLiteralNode || n instanceof LocalVariableNode;
        contents.put(n, val);
    }

    @Override
    @SuppressWarnings(
            "determinism") // valid rule relaxation: copy clearly preserves determinism type
    public ConstantPropagationStore copy() {
        // true positive; `contents` is declared as a Det map but is assigned an OrderNonDet HashMap
        // Fixed: https://github.com/typetools/checker-framework/commit/0a0ea1021409ca8d2d5d7e7f57556ed45cd2472d
        return new ConstantPropagationStore(new HashMap<>(contents));
    }

    @Override
    @SuppressWarnings(
            "determinism") // process is order insensitive: calculating a least upper bound
    public ConstantPropagationStore leastUpperBound(ConstantPropagationStore other) {

        // true positive; `contents` is declared as a Det map but is assigned an OrderNonDet HashMap
        // Fixed: https://github.com/typetools/checker-framework/commit/0a0ea1021409ca8d2d5d7e7f57556ed45cd2472d
        Map<Node, Constant> newContents = new HashMap<>();

        // go through all of the information of the other class
        for (Map.Entry<Node, Constant> e : other.contents.entrySet()) {
            Node n = e.getKey();
            Constant otherVal = e.getValue();
            if (contents.containsKey(n)) {
                // merge if both contain information about a variable
                newContents.put(n, otherVal.leastUpperBound(contents.get(n)));
            } else {
                // add new information
                newContents.put(n, otherVal);
            }
        }

        for (Map.Entry<Node, Constant> e : contents.entrySet()) {
            Node n = e.getKey();
            Constant thisVal = e.getValue();
            if (!other.contents.containsKey(n)) {
                // add new information
                newContents.put(n, thisVal);
            }
        }

        return new ConstantPropagationStore(newContents);
    }

    @Override
    public ConstantPropagationStore widenedUpperBound(ConstantPropagationStore previous) {
        return leastUpperBound(previous);
    }

    @SuppressWarnings(
            "determinism:enhancedfor.type.incompatible" // collection is not @OrderNonDet, so
    // elements are @PolyDet
    )
    @Override
    public @PolyDet boolean equals(
            @PolyDet ConstantPropagationStore this, @PolyDet @Nullable Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof ConstantPropagationStore)) {
            return false;
        }
        ConstantPropagationStore other = (ConstantPropagationStore) o;
        // go through all of the information of the other object
        for (Map.@PolyDet Entry<Node, Constant> e : other.contents.entrySet()) {
            Node n = e.getKey();
            Constant otherVal = e.getValue();
            if (otherVal.isBottom()) {
                continue; // no information
            }
            if (contents.containsKey(n)) {
                if (!otherVal.equals(contents.get(n))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        // go through all of the information of the this object
        for (Map.@PolyDet Entry<Node, Constant> e : contents.entrySet()) {
            Node n = e.getKey();
            Constant thisVal = e.getValue();
            if (thisVal.isBottom()) {
                continue; // no information
            }
            if (other.contents.containsKey(n)) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings(
            "determinism" // collection is not @OrderNonDet, so elements are @PolyDet; also,
    // hashCode of Entry<Node, Constant> is @PolyDet because Node and Constant
    // override hashCode
    )
    @Override
    public @PolyDet int hashCode(@PolyDet ConstantPropagationStore this) {
        @PolyDet int s = 0;
        for (Map.@PolyDet Entry<Node, Constant> e : contents.entrySet()) {
            if (!e.getValue().isBottom()) {
                s += e.hashCode();
            }
        }
        return s;
    }

    @SuppressWarnings(
            "determinism" // collection is not @OrderNonDet, so elements are @PolyDet; also,
    // toString of Entry<Node, Constant> is @PolyDet because Node and Constant override toString
    )
    @Override
    public @PolyDet String toString(@PolyDet ConstantPropagationStore this) {
        // only output local variable information
        // true positive; `contents` is declared as a Det map but is assigned an OrderNonDet HashMap
        // Fixed: https://github.com/typetools/checker-framework/commit/0a0ea1021409ca8d2d5d7e7f57556ed45cd2472d
        Map<Node, Constant> smallerContents = new HashMap<>();
        for (Map.Entry<Node, Constant> e : contents.entrySet()) {
            if (e.getKey() instanceof LocalVariableNode) {
                smallerContents.put(e.getKey(), e.getValue());
            }
        }
        return smallerContents.toString();
    }

    @Override
    public boolean canAlias(Receiver a, Receiver b) {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>{@code value} is {@code null} because {@link ConstantPropagationStore} doesn't support
     * visualization.
     */
    @Override
    @SuppressWarnings("nullness")
    public String visualize(
            ConstantPropagationStore this, CFGVisualizer<?, ConstantPropagationStore, ?> viz) {
        return viz.visualizeStoreKeyVal("constant propagation", null);
    }
}
