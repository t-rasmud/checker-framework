package org.checkerframework.checker.nonempty;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;

public class NonEmptyChecker extends BaseTypeChecker {
    @Override
    public GenericAnnotatedTypeFactory<?, ?, ?, ?> getTypeFactory() {
        GenericAnnotatedTypeFactory<?, ?, ?, ?> result = super.getTypeFactory();
        result.sideEffectsUnrefineAliases = true;
        return result;
    }
}
