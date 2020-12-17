package org.checkerframework.checker.nonempty;

import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.GreaterThanNode;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;

public class NonEmptyTransfer extends CFTransfer {
    public NonEmptyTransfer(CFAnalysis analysis) {
        super(analysis);
    }

    @Override
    public TransferResult<CFValue, CFStore> visitGreaterThan(
            GreaterThanNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput) {
        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            System.out.println("<<< " + leftOp);
            System.out.println(">>> " + rightOp + " : " + rightOp.getType());
        }
        return super.visitGreaterThan(n, cfValueCFStoreTransferInput);
    }
}
