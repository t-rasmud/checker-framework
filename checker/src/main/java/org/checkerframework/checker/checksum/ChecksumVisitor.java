package org.checkerframework.checker.checksum;

import java.util.Collections;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;

public class ChecksumVisitor extends BaseTypeVisitor<ChecksumAnnotatedTypeFactory> {
    public ChecksumVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    protected ChecksumAnnotatedTypeFactory createTypeFactory() {
        return new ChecksumAnnotatedTypeFactory(checker);
    }

    @Override
    protected void checkConstructorResult(
            AnnotatedTypeMirror.AnnotatedExecutableType constructorType,
            ExecutableElement constructorElement) {
        if (constructorType.getReturnType().hasAnnotation(atypeFactory.NOT_CHECKSUMMED)) {
            return;
        }
        checker.reportError(constructorElement, "invalid.constructor.return.type");
    }

    @Override
    protected Set<? extends AnnotationMirror> getExceptionParameterLowerBoundAnnotations() {
        return Collections.singleton(atypeFactory.NOT_CHECKSUMMED);
    }
}
