package org.checkerframework.checker.tainting;

import com.sun.source.tree.Tree;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedDeclaredType;
import org.checkerframework.framework.type.AnnotatedTypeMirror.AnnotatedExecutableType;

/** Issues errors for the Tainting Checker. */
public class TaintingVisitor extends BaseTypeVisitor<TaintingAnnotatedTypeFactory> {

    /** Creates a {@link TaintingVisitor}. */
    public TaintingVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    protected void checkConstructorResult(
            AnnotatedExecutableType constructorType, ExecutableElement constructorElement) {
        // skip
    }

    @Override
    public boolean isValidUse(
            AnnotatedDeclaredType declarationType, AnnotatedDeclaredType useType, Tree tree) {
        // skip!
        return true;
    }
}
