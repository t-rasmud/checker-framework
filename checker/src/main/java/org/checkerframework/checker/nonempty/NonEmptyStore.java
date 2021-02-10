package org.checkerframework.checker.nonempty;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAbstractStore;
import org.checkerframework.framework.qual.JavaExpression;

public class NonEmptyStore extends CFAbstractStore<NonEmptyValue, NonEmptyStore> {
    protected Map<String, JavaExpression> sizeEqualitiesMap;

    protected NonEmptyStore(
            CFAbstractAnalysis<NonEmptyValue, NonEmptyStore, ?> analysis,
            boolean sequentialSemantics) {
        super(analysis, sequentialSemantics);
        sizeEqualitiesMap = new HashMap<>();
    }

    protected NonEmptyStore(CFAbstractStore<NonEmptyValue, NonEmptyStore> other) {
        super(other);
    }

    public Map<String, JavaExpression> getSizeEqualitiesMap() {
        return sizeEqualitiesMap;
    }
}
