package org.checkerframework.checker.nonempty;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;

/**
 * The NonEmpty Checker ensures that a Collection, Map, or an Iterator annotated
 * as {@code @NonEmpty} contains atleast one element.
 */
public class NonEmptyChecker extends BaseTypeChecker {
    @Override
    public GenericAnnotatedTypeFactory<?, ?, ?, ?> getTypeFactory() {
        GenericAnnotatedTypeFactory<?, ?, ?, ?> result = super.getTypeFactory();
        result.sideEffectsUnrefineAliases = true;
        return result;
    }
}
