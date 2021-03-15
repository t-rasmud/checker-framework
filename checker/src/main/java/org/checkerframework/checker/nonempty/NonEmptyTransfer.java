package org.checkerframework.checker.nonempty;

import com.sun.source.tree.Tree;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.dataflow.analysis.ConditionalTransferResult;
import org.checkerframework.dataflow.analysis.TransferInput;
import org.checkerframework.dataflow.analysis.TransferResult;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.ConditionalNotNode;
import org.checkerframework.dataflow.cfg.node.EqualToNode;
import org.checkerframework.dataflow.cfg.node.GreaterThanNode;
import org.checkerframework.dataflow.cfg.node.GreaterThanOrEqualNode;
import org.checkerframework.dataflow.cfg.node.IntegerLiteralNode;
import org.checkerframework.dataflow.cfg.node.LessThanNode;
import org.checkerframework.dataflow.cfg.node.LessThanOrEqualNode;
import org.checkerframework.dataflow.cfg.node.MethodInvocationNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.expression.JavaExpression;
import org.checkerframework.dataflow.util.NodeUtils;
import org.checkerframework.framework.flow.CFAbstractTransfer;
import org.checkerframework.framework.type.AnnotatedTypeFactory;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;

/** Defines transfer functions for the NonEmpty Checker. */
public class NonEmptyTransfer
        extends CFAbstractTransfer<NonEmptyValue, NonEmptyStore, NonEmptyTransfer> {

    /** Collection.size() method. */
    private final ExecutableElement sizeMethod;
    /** Collection.isEmpty() method. */
    private final ExecutableElement isEmptyMethod;
    /** Processing environment. */
    ProcessingEnvironment processingEnv;
    /** The BaseTypeFactory. */
    AnnotatedTypeFactory atypeFactory;
    /** The {@literal @}{@link NonEmpty} annotation. */
    AnnotationMirror NONEMPTY;

    /**
     * Constructor for NonEmptyTransfer.
     *
     * @param analysis CFAnalysis
     */
    public NonEmptyTransfer(NonEmptyAnalysis analysis) {
        super(analysis);
        atypeFactory = analysis.getTypeFactory();
        processingEnv = atypeFactory.getProcessingEnv();
        this.sizeMethod = TreeUtils.getMethod("java.util.Collection", "size", 0, processingEnv);
        this.isEmptyMethod =
                TreeUtils.getMethod("java.util.Collection", "isEmpty", 0, processingEnv);
        NONEMPTY = new AnnotationBuilder(processingEnv, NonEmpty.class).build();
    }

    /**
     * For a conditional that checks {@code Collection.size() > x} where {@code x >= 0}, refines the
     * type of the {@code Collection} to {@code @NonEmpty} in the then branch.
     *
     * @param n GreaterThanNode
     * @param cfValueCFStoreTransferInput TransferResult input
     * @return TransferResult with possibly refined stores
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitGreaterThan(
            GreaterThanNode n,
            TransferInput<NonEmptyValue, NonEmptyStore> cfValueCFStoreTransferInput) {
        TransferResult<NonEmptyValue, NonEmptyStore> resultIn =
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
                    return refineThenStore(leftOp, resultIn);
                }
            }
        }
        return resultIn;
    }

    /**
     * For a conditional that checks {@code Collection.size() >= x} where {@code x >= 1}, refines
     * the type of the {@code Collection} to {@code @NonEmpty} in the then branch.
     *
     * @param n GreaterThanOrEqualNode
     * @param cfValueCFStoreTransferInput TransferResult input
     * @return TransferResult with possibly refined stores
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitGreaterThanOrEqual(
            GreaterThanOrEqualNode n,
            TransferInput<NonEmptyValue, NonEmptyStore> cfValueCFStoreTransferInput) {
        TransferResult<NonEmptyValue, NonEmptyStore> resultIn =
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
                    return refineThenStore(leftOp, resultIn);
                }
            }
        }
        return resultIn;
    }

    /**
     * For a conditional that checks {@code Collection.size() < 1}, refines the type of the {@code
     * Collection} to {@code @NonEmpty} in the else branch.
     *
     * @param n LessThanNode
     * @param cfValueCFStoreTransferInput TransferResult input
     * @return TransferResult with possibly refined stores
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitLessThan(
            LessThanNode n,
            TransferInput<NonEmptyValue, NonEmptyStore> cfValueCFStoreTransferInput) {
        TransferResult<NonEmptyValue, NonEmptyStore> resultIn =
                super.visitLessThan(n, cfValueCFStoreTransferInput);

        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(leftOp, sizeMethod, processingEnv)) {
                if (!(rightOp instanceof IntegerLiteralNode)) {
                    return resultIn;
                }
                int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
                if (rightOpInt == 1) {
                    return refineElseStore(leftOp, resultIn);
                }
            }
        }
        return resultIn;
    }

    /**
     * For a conditional that checks {@code Collection.size() <= x} where {@code x <= 1}, refines
     * the type of the {@code Collection} to {@code @NonEmpty} in the else branch. This is an
     * over-approximation. The precise refinement would check for {@code x == 0}. This
     * over-approximation is added based on observed code examples in benchmarks.
     *
     * @param n LessThanOrEqualNode
     * @param cfValueCFStoreTransferInput TransferResult input
     * @return TransferResult with possibly refined stores
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitLessThanOrEqual(
            LessThanOrEqualNode n,
            TransferInput<NonEmptyValue, NonEmptyStore> cfValueCFStoreTransferInput) {
        TransferResult<NonEmptyValue, NonEmptyStore> resultIn =
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
                    return refineElseStore(leftOp, resultIn);
                }
            }
        }
        return resultIn;
    }

    /**
     * For a conditional that checks {@code Collection.size() == x} where {@code x >= 1}, refines
     * the type of the {@code Collection} to {@code @NonEmpty} in the then branch.
     *
     * <p>For a conditional that checks {@code Collection.size() == x} where {@code x == 1}, refines
     * the type of the {@code Collection} to {@code @NonEmpty} in the else branch.
     *
     * <p>For a conditional that checks {@code size == x} where {@code x >= 1} and size =
     * Collection.size(), refines the type of the {@code Collection} to {@code @NonEmpty} in the
     * then branch.
     *
     * <p>For a conditional that checks {@code size == x} where {@code x == 0} and size =
     * Collection.size(), refines the type of the {@code Collection} to {@code @NonEmpty} in the
     * else branch.
     *
     * @param n EqualToNode
     * @param p TransferResult input
     * @return TransferResult with possibly refined stores
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitEqualTo(
            EqualToNode n, TransferInput<NonEmptyValue, NonEmptyStore> p) {
        TransferResult<NonEmptyValue, NonEmptyStore> resultIn = super.visitEqualTo(n, p);

        Node leftOp = n.getLeftOperand();
        Node rightOp = n.getRightOperand();
        if (leftOp instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(leftOp, sizeMethod, processingEnv)) {
                if (!(rightOp instanceof IntegerLiteralNode)) {
                    return resultIn;
                }
                int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
                if (rightOpInt >= 1) {
                    return refineThenStore(leftOp, resultIn);
                }

                if (rightOpInt == 0) {
                    return refineElseStore(leftOp, resultIn);
                }
            }
        }
        NonEmptyStore store = resultIn.getRegularStore();
        Map<String, Node> sizeEqMap = store.getSizeEqualitiesMap();
        String mapKey = leftOp.toString();
        if (sizeEqMap != null && sizeEqMap.containsKey(mapKey)) {
            Node mapVal = sizeEqMap.get(mapKey);
            if (!(rightOp instanceof IntegerLiteralNode)) {
                return resultIn;
            }
            int rightOpInt = ((IntegerLiteralNode) rightOp).getValue();
            if (rightOpInt >= 1) {
                return refineThenStore(mapVal, resultIn);
            }
            if (rightOpInt == 0) {
                return refineElseStore(mapVal, resultIn);
            }
        }
        return resultIn;
    }

    /**
     * For a conditional that checks {@code !Collection.isEmpty()}, refines the type of the {@code
     * Collection} to {@code @NonEmpty} in the then branch.
     *
     * @param n ConditionalNotNode
     * @param cfValueCFStoreTransferInput TransferResult input
     * @return TransferResult with possibly refined stores
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitConditionalNot(
            ConditionalNotNode n,
            TransferInput<NonEmptyValue, NonEmptyStore> cfValueCFStoreTransferInput) {
        TransferResult<NonEmptyValue, NonEmptyStore> transferResult =
                super.visitConditionalNot(n, cfValueCFStoreTransferInput);
        Node operand = n.getOperand();
        if (operand instanceof MethodInvocationNode) {
            if (NodeUtils.isMethodInvocation(operand, isEmptyMethod, processingEnv)) {
                return refineThenStore(operand, transferResult);
            }
        }
        return transferResult;
    }

    /**
     * Refines the type of receiver in {@code operand} to {@code @NonEmpty} in the then store.
     *
     * @param operand Node whose type qualifier is to be refined
     * @param resultIn TransferResult input
     * @return TransferResult with the refined type inserted in the then store
     */
    TransferResult<NonEmptyValue, NonEmptyStore> refineThenStore(
            Node operand, TransferResult<NonEmptyValue, NonEmptyStore> resultIn) {
        Node leftReceiver = ((MethodInvocationNode) operand).getTarget().getReceiver();
        JavaExpression leftRec = JavaExpression.fromNode(atypeFactory, leftReceiver);

        NonEmptyStore thenStore = resultIn.getRegularStore();
        NonEmptyStore elseStore = thenStore.copy();
        ConditionalTransferResult<NonEmptyValue, NonEmptyStore> newResult =
                new ConditionalTransferResult<>(resultIn.getResultValue(), thenStore, elseStore);

        thenStore.insertValue(leftRec, NONEMPTY);
        return newResult;
    }

    /**
     * Refines the type of receiver in {@code operand} to {@code @NonEmpty} in the else store.
     *
     * @param operand Node whose type qualifier is to be refined
     * @param resultIn TransferResult input
     * @return TransferResult with the refined type inserted in the else store
     */
    TransferResult<NonEmptyValue, NonEmptyStore> refineElseStore(
            Node operand, TransferResult<NonEmptyValue, NonEmptyStore> resultIn) {
        Node leftReceiver = ((MethodInvocationNode) operand).getTarget().getReceiver();
        JavaExpression leftRec = JavaExpression.fromNode(atypeFactory, leftReceiver);

        NonEmptyStore thenStore = resultIn.getRegularStore();
        NonEmptyStore elseStore = thenStore.copy();
        ConditionalTransferResult<NonEmptyValue, NonEmptyStore> newResult =
                new ConditionalTransferResult<>(resultIn.getResultValue(), thenStore, elseStore);

        elseStore.insertValue(leftRec, NONEMPTY);
        return newResult;
    }

    /**
     * Updates the {@code sizeEqualitiesMap} in the NonEmptyStore as follows:
     *
     * <p>If the assignment statement assigns {@code collection.size()} to {@code size}, adds {@code
     * size} as the key and {@code collection.size()} as the value.
     *
     * <p>For any other update to {@code size}, removes {@code size} from the map.
     *
     * @param n AssignmentNode
     * @param in TransferInput input
     * @return TransferResult with the updated NonEmptyStore
     */
    @Override
    public TransferResult<NonEmptyValue, NonEmptyStore> visitAssignment(
            AssignmentNode n, TransferInput<NonEmptyValue, NonEmptyStore> in) {
        Tree.Kind targetKind = n.getTarget().getTree().getKind();
        if (targetKind == Tree.Kind.VARIABLE || targetKind == Tree.Kind.IDENTIFIER) {
            NonEmptyStore store = in.getRegularStore();
            Node leftReceiver = n.getTarget();
            JavaExpression leftRec = JavaExpression.fromNode(atypeFactory, leftReceiver);
            Map<String, Node> sizeEqMap = store.getSizeEqualitiesMap();
            String mapKey = leftRec.toString();
            if (NodeUtils.isMethodInvocation(n.getExpression(), sizeMethod, processingEnv)) {
                Node rightReceiver = n.getExpression();
                if (sizeEqMap == null) {
                    store.createSizeEqualifiesMap();
                    sizeEqMap = store.getSizeEqualitiesMap();
                }
                sizeEqMap.put(mapKey, rightReceiver);
            } else if (sizeEqMap != null && sizeEqMap.containsKey(mapKey)) {
                store.getSizeEqualitiesMap().remove(mapKey);
            }
        }
        return super.visitAssignment(n, in);
    }
}
