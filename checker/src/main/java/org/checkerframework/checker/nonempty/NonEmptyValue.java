package org.checkerframework.checker.nonempty;

import java.util.Map;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAbstractValue;

public class NonEmptyValue extends CFAbstractValue<NonEmptyValue> {
    protected static Map<String, Node> sizeEqualitiesMap;

    /**
     * Creates a new CFAbstractValue.
     *
     * @param analysis the analysis class this value belongs to
     * @param annotations the annotations in this abstract value
     * @param underlyingType the underlying (Java) type in this abstract value
     */
    protected NonEmptyValue(
            CFAbstractAnalysis<NonEmptyValue, ?, ?> analysis,
            Set<AnnotationMirror> annotations,
            TypeMirror underlyingType) {
        super(analysis, annotations, underlyingType);
    }
}
