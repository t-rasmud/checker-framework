package org.checkerframework.checker.nonempty;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAbstractStore;

public class NonEmptyStore extends CFAbstractStore<NonEmptyValue, NonEmptyStore> {
    protected static Map<String, Node> sizeEqualitiesMap;

    protected NonEmptyStore(
            CFAbstractAnalysis<NonEmptyValue, NonEmptyStore, ?> analysis,
            boolean sequentialSemantics) {
        super(analysis, sequentialSemantics);
    }

    protected void createSizeEqualifiesMap() {
        sizeEqualitiesMap = new HashMap<>();
    }

    protected NonEmptyStore(NonEmptyStore other) {
        super(other);
    }

    public Map<String, Node> getSizeEqualitiesMap() {
        return sizeEqualitiesMap;
    }
}
