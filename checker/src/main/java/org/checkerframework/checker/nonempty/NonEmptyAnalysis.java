package org.checkerframework.checker.nonempty;

import java.util.List;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAbstractValue;
import org.checkerframework.javacutil.Pair;

public class NonEmptyAnalysis
        extends CFAbstractAnalysis<NonEmptyValue, NonEmptyStore, NonEmptyTransfer> {

    protected NonEmptyAnalysis(
            BaseTypeChecker checker,
            NonEmptyAnnotatedTypeFactory factory,
            List<Pair<VariableElement, NonEmptyValue>> fieldValues,
            int maxCountBeforeWidening) {
        super(checker, factory, fieldValues, maxCountBeforeWidening);
    }

    protected NonEmptyAnalysis(
            BaseTypeChecker checker,
            NonEmptyAnnotatedTypeFactory factory,
            List<Pair<VariableElement, NonEmptyValue>> fieldValues) {
        super(checker, factory, fieldValues);
    }

    @Override
    public NonEmptyStore createEmptyStore(boolean sequentialSemantics) {
        return new NonEmptyStore(this, sequentialSemantics);
    }

    @Override
    public NonEmptyStore createCopiedStore(NonEmptyStore nonEmptyStore) {
        return new NonEmptyStore(nonEmptyStore);
    }

    @Override
    public NonEmptyValue createAbstractValue(
            Set<AnnotationMirror> annotations, TypeMirror underlyingType) {
        if (!CFAbstractValue.validateSet(annotations, underlyingType, qualifierHierarchy)) {
            return null;
        }
        return new NonEmptyValue(this, annotations, underlyingType);
    }
}
