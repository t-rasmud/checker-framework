package org.checkerframework.dataflow.cfg.builder;

import java.util.Set;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

/** A TryFinallyFrame applies to exceptions of any type. */
class TryFinallyFrame implements TryFrame {
    /** The finally label. */
    protected final Label finallyLabel;

    /**
     * Construct a TryFinallyFrame.
     *
     * @param finallyLabel finally label
     */
    public TryFinallyFrame(Label finallyLabel) {
        this.finallyLabel = finallyLabel;
    }

    @Override
    public @PolyDet String toString(@PolyDet TryFinallyFrame this) {
        return "TryFinallyFrame: finallyLabel: " + finallyLabel;
    }

    @Override
    public boolean possibleLabels(TypeMirror thrown, @OrderNonDet Set<Label> labels) {
        labels.add(finallyLabel);
        return true;
    }
}
