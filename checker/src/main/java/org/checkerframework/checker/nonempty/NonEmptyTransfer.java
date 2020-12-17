package org.checkerframework.checker.nonempty;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.dataflow.analysis.ConditionalTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.ConditionalNotNode;
import org.checkerframework.dataflow.cfg.node.EqualToNode;
import org.checkerframework.dataflow.cfg.node.GreaterThanNode;
import org.checkerframework.dataflow.cfg.node.GreaterThanOrEqualNode;
import org.checkerframework.dataflow.cfg.node.IntegerLiteralNode;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.expression.FlowExpressions;
import org.checkerframework.dataflow.expression.Receiver;
import org.checkerframework.dataflow.util.NodeUtils;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;

public class NonEmptyTransfer extends CFTransfer {
    private final ExecutableElement sizeMethod;
    private final ExecutableElement isEmptyMethod;
    ProcessingEnvironment processingEnv;
    AnnotatedTypeFactory atypeFactory;

    public NonEmptyTransfer(CFAnalysis analysis) {
        super(analysis);
        atypeFactory = analysis.getTypeFactory();
        processingEnv = atypeFactory.getProcessingEnv();
        this.sizeMethod = TreeUtils.getMethod("java.util.Collection", "size", 0, processingEnv);
        this.isEmptyMethod =
                TreeUtils.getMethod("java.util.Collection", "isEmpty", 0, processingEnv);
    }

    @Override
    public TransferResult<CFValue, CFStore> visitGreaterThan(
            GreaterThanNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput) {
        TransferResult<CFValue, CFStore> resultIn =
                super.visitGreaterThan(n, cfValueCFStoreTransferInput);

        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(leftOp, sizeMethod, processingEnv)) {
                if (!(rightOp instanceof IntegerLiteralNode)) {
                    return resultIn;
                }
                int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
                if (rightOpInt >= 0) {
                    Node leftReceiver = ((MethodInvocationNode) leftOp).getTarget().getReceiver();
                    Receiver leftRec = FlowExpressions.internalReprOf(atypeFactory, leftReceiver);

                    CFStore thenStore = resultIn.getRegularStore();
                    CFStore elseStore = thenStore.copy();
                    ConditionalTransferResult<CFValue, CFStore> newResult =
                            new ConditionalTransferResult<>(
                                    resultIn.getResultValue(), thenStore, elseStore);

                    AnnotationBuilder builder =
                            new AnnotationBuilder(processingEnv, NonEmpty.class);
                    AnnotationMirror nonEmptyAnnotation = builder.build();
                    thenStore.insertValue(leftRec, nonEmptyAnnotation);
                    return newResult;
                }
            }
        }
        return resultIn;
    }

    @Override
    public TransferResult<CFValue, CFStore> visitGreaterThanOrEqual(
            GreaterThanOrEqualNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput) {
        TransferResult<CFValue, CFStore> resultIn =
                super.visitGreaterThanOrEqual(n, cfValueCFStoreTransferInput);

        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(leftOp, sizeMethod, processingEnv)) {
                if (!(rightOp instanceof IntegerLiteralNode)) {
                    return resultIn;
                }
                int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
                if (rightOpInt >= 1) {
                    Node leftReceiver = ((MethodInvocationNode) leftOp).getTarget().getReceiver();
                    Receiver leftRec = FlowExpressions.internalReprOf(atypeFactory, leftReceiver);

                    CFStore thenStore = resultIn.getRegularStore();
                    CFStore elseStore = thenStore.copy();
                    ConditionalTransferResult<CFValue, CFStore> newResult =
                            new ConditionalTransferResult<>(
                                    resultIn.getResultValue(), thenStore, elseStore);

                    AnnotationBuilder builder =
                            new AnnotationBuilder(processingEnv, NonEmpty.class);
                    AnnotationMirror nonEmptyAnnotation = builder.build();
                    thenStore.insertValue(leftRec, nonEmptyAnnotation);
                    return newResult;
                }
            }
        }
        return resultIn;
    }

    @Override
    public TransferResult<CFValue, CFStore> visitLessThanThanOrEqual(
            GreaterThanOrEqualNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput) {
        TransferResult<CFValue, CFStore> resultIn =
                super.visitLessThanOrEqual(n, cfValueCFStoreTransferInput);

        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(leftOp, sizeMethod, processingEnv)) {
                if (!(rightOp instanceof IntegerLiteralNode)) {
                    return resultIn;
                }
                int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
                if (rightOpInt <= 1) {
                    Node leftReceiver = ((MethodInvocationNode) leftOp).getTarget().getReceiver();
                    Receiver leftRec = FlowExpressions.internalReprOf(atypeFactory, leftReceiver);

                    CFStore thenStore = resultIn.getRegularStore();
                    CFStore elseStore = thenStore.copy();
                    ConditionalTransferResult<CFValue, CFStore> newResult =
                            new ConditionalTransferResult<>(
                                    resultIn.getResultValue(), thenStore, elseStore);

                    AnnotationBuilder builder =
                            new AnnotationBuilder(processingEnv, NonEmpty.class);
                    AnnotationMirror nonEmptyAnnotation = builder.build();
                    elseStore.insertValue(leftRec, nonEmptyAnnotation);
                    return newResult;
                }
            }
        }
        return resultIn;
    }

    @Override
    public TransferResult<CFValue, CFStore> visitEqualTo(
            EqualToNode n, TransferInput<CFValue, CFStore> p) {
        TransferResult<CFValue, CFStore> resultIn = super.visitEqualTo(n, p);

        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(leftOp, sizeMethod, processingEnv)) {
                if (!(rightOp instanceof IntegerLiteralNode)) {
                    return resultIn;
                }
                int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
                if (rightOpInt >= 1) {
                    Node leftReceiver = ((MethodInvocationNode) leftOp).getTarget().getReceiver();
                    Receiver leftRec = FlowExpressions.internalReprOf(atypeFactory, leftReceiver);

                    CFStore thenStore = resultIn.getRegularStore();
                    CFStore elseStore = thenStore.copy();
                    ConditionalTransferResult<CFValue, CFStore> newResult =
                            new ConditionalTransferResult<>(
                                    resultIn.getResultValue(), thenStore, elseStore);

                    AnnotationBuilder builder =
                            new AnnotationBuilder(processingEnv, NonEmpty.class);
                    AnnotationMirror nonEmptyAnnotation = builder.build();
                    thenStore.insertValue(leftRec, nonEmptyAnnotation);
                    return newResult;
                }
            }
        }
        return resultIn;
    }

    @Override
    public TransferResult<CFValue, CFStore> visitConditionalNot(
            ConditionalNotNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput) {
        TransferResult<CFValue, CFStore> transferResult =
                super.visitConditionalNot(n, cfValueCFStoreTransferInput);
        Node operand = n.getOperand();
        if (operand instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(operand, isEmptyMethod, processingEnv)) {
                Node receiver = ((MethodInvocationNode) operand).getTarget().getReceiver();
                Receiver rec = FlowExpressions.internalReprOf(atypeFactory, receiver);

                CFStore thenStore = transferResult.getThenStore();
                CFStore elseStore = transferResult.getElseStore();
                ConditionalTransferResult<CFValue, CFStore> newResult =
                        new ConditionalTransferResult<>(
                                transferResult.getResultValue(), thenStore, elseStore);

                AnnotationBuilder builder = new AnnotationBuilder(processingEnv, NonEmpty.class);
                AnnotationMirror nonEmptyAnnotation = builder.build();
                thenStore.insertValue(rec, nonEmptyAnnotation);
                return newResult;
            }
        }
        return transferResult;
    }
}
