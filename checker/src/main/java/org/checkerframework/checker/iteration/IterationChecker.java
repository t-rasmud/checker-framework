package org.checkerframework.checker.iteration;

import org.checkerframework.checker.iteration.nonempty.NonEmptyChecker;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;

import java.util.LinkedHashSet;

/** The Iteration Checker ensures that Iterator.next() does not throw NoSuchElementException. */
public class IterationChecker extends BaseTypeChecker {

    @Override
    public GenericAnnotatedTypeFactory<?, ?, ?, ?> getTypeFactory() {
        GenericAnnotatedTypeFactory<?, ?, ?, ?> result = super.getTypeFactory();
        result.sideEffectsUnrefineAliases = true;
        return result;
    }

    @Override
    protected LinkedHashSet<Class<? extends BaseTypeChecker>> getImmediateSubcheckerClasses() {
        LinkedHashSet<Class<? extends BaseTypeChecker>> checkers =
            super.getImmediateSubcheckerClasses();
        checkers.add(NonEmptyChecker.class);
        return checkers;
    }
}
