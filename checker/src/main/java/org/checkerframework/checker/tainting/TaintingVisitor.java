package org.checkerframework.checker.tainting;

import javax.lang.model.element.ExecutableElement;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;

/** Visitor for the {@link TaintingChecker}. */
public class TaintingVisitor extends BaseTypeVisitor<BaseAnnotatedTypeFactory> {

    /** Creates a {@link TaintingVisitor}. */
    public TaintingVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    /**
     * Don't check the constructor result. Checking that the super() or this() call is a subtype of
     * the constructor result is sufficient.
     */
    @Override
    protected void checkConstructorResult(
            AnnotatedExecutableType constructorType, ExecutableElement constructorElement) {}
}
