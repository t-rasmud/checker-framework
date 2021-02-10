package org.checkerframework.checker.nonempty;

// import java.util.LinkedHashSet;
// import org.checkerframework.checker.sizeof.SizeOfChecker;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;

/**
 * The NonEmpty Checker ensures that a Collection, Map, or an Iterator annotated as
 * {@code @NonEmpty} contains at least one element.
 */
public class NonEmptyChecker extends BaseTypeChecker {
    @Override
    public GenericAnnotatedTypeFactory<?, ?, ?, ?> getTypeFactory() {
        GenericAnnotatedTypeFactory<?, ?, ?, ?> result = super.getTypeFactory();
        result.sideEffectsUnrefineAliases = true;
        return super.getTypeFactory();
    }
}
