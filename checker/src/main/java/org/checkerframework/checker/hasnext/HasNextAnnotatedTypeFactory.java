package org.checkerframework.checker.hasnext;

import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.hasnext.qual.HasNextTrue;
import org.checkerframework.checker.hasnext.qual.UnknownHasNext;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.*;
import org.checkerframework.javacutil.AnnotationBuilder;

public class HasNextAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    public final AnnotationMirror UNKNOWNHASNEXT =
            AnnotationBuilder.fromClass(elements, UnknownHasNext.class);
    public final AnnotationMirror HASNEXTTRUE =
            AnnotationBuilder.fromClass(elements, HasNextTrue.class);

    public HasNextAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    public CFTransfer createFlowTransferFunction(
            CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        return new HasNextTransfer((CFAnalysis) analysis);
    }
}
