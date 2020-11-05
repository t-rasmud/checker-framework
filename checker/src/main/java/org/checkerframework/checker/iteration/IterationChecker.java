package org.checkerframework.checker.iteration;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;

/** The Iteration Checker ensures that Iterator.next() does not throw NoSuchElementException. */
public class IterationChecker extends BaseTypeChecker {

    @Override
    public GenericAnnotatedTypeFactory<?, ?, ?, ?> getTypeFactory() {
        GenericAnnotatedTypeFactory<?, ?, ?, ?> result = super.getTypeFactory();
        result.sideEffectsUnrefineAliases = true;
        return result;
    }
}
