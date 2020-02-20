package org.checkerframework.checker.determinism;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.FlowExpressions;
import org.checkerframework.framework.flow.CFAbstractStore;
import org.checkerframework.framework.flow.CFAbstractValue;

public class DeterminismStore<V extends CFAbstractValue<V>, S extends DeterminismStore<V, S>>
        extends CFAbstractStore<V, S> {
    protected DeterminismStore(CFAbstractStore<V, S> other) {
        super(other);
    }

    @Override
    public void insertValue(FlowExpressions.Receiver r, @Nullable V value) {
        if (r instanceof FlowExpressions.ThisReference) {
            FlowExpressions.ThisReference thisRef = (FlowExpressions.ThisReference) r;
            if (sequentialSemantics || thisRef.isUnassignableByOtherCode()) {
                V oldValue = thisValue;
                V newValue = value;
                if (newValue != null) {
                    thisValue = newValue;
                    return;
                }
            }
        }
        super.insertValue(r, value);
    }
}
