package org.checkerframework.checker.hasnext;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.util.Elements;
import org.checkerframework.checker.hasnext.qual.UnknownHasNext;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.expression.FlowExpressions;
import org.checkerframework.dataflow.expression.Receiver;
import org.checkerframework.dataflow.util.NodeUtils;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;

public class HasNextTransfer extends CFTransfer {
    protected final AnnotationMirror UNKNOWNHASNEXT;

    public HasNextTransfer(CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        super(analysis);
        Elements elements =
                ((HasNextAnnotatedTypeFactory) analysis.getTypeFactory()).getElementUtils();
        UNKNOWNHASNEXT = AnnotationBuilder.fromClass(elements, UnknownHasNext.class);
    }

    @Override
    public TransferResult<CFValue, CFStore> visitMethodInvocation(
            MethodInvocationNode n, TransferInput<CFValue, CFStore> in) {
        HasNextAnnotatedTypeFactory factory =
                (HasNextAnnotatedTypeFactory) analysis.getTypeFactory();
        TransferResult<CFValue, CFStore> result = super.visitMethodInvocation(n, in);
        ExecutableElement iteratorNext =
                TreeUtils.getMethod("java.util.Iterator", "next", 0, factory.getProcessingEnv());
        if (NodeUtils.isMethodInvocation(n, iteratorNext, factory.getProcessingEnv())) {
            Receiver receiver = FlowExpressions.internalReprOf(factory, n);
            if (result.containsTwoStores()) {
                result.getThenStore().clearValue(receiver);
                result.getThenStore().insertValue(receiver, UNKNOWNHASNEXT);
                result.getElseStore().clearValue(receiver);
                result.getElseStore().insertValue(receiver, UNKNOWNHASNEXT);
            } else {
                result.getRegularStore().clearValue(receiver);
                result.getRegularStore().insertValue(receiver, UNKNOWNHASNEXT);
            }
        }
        return result;
    }
}
