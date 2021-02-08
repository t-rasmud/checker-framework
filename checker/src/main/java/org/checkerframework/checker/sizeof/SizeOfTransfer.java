// package org.checkerframework.checker.sizeof;
//
// import java.util.Collection;
// import javax.annotation.processing.ProcessingEnvironment;
// import javax.lang.model.element.AnnotationMirror;
//
// import com.sun.source.tree.Tree;
// import org.checkerframework.checker.sizeof.qual.SizeOf;
// import org.checkerframework.checker.sizeof.qual.UnknownSizeOf;
// import org.checkerframework.dataflow.analysis.ConditionalTransferResult;
// import org.checkerframework.dataflow.analysis.RegularTransferResult;
// import org.checkerframework.dataflow.analysis.TransferInput;
// import org.checkerframework.dataflow.analysis.TransferResult;
// import org.checkerframework.dataflow.cfg.node.AssignmentNode;
// import org.checkerframework.dataflow.cfg.node.GreaterThanOrEqualNode;
// import org.checkerframework.dataflow.cfg.node.LessThanNode;
// import org.checkerframework.dataflow.cfg.node.Node;
// import org.checkerframework.dataflow.expression.FlowExpressions;
// import org.checkerframework.dataflow.expression.Receiver;
// import org.checkerframework.framework.flow.CFAnalysis;
// import org.checkerframework.framework.flow.CFStore;
// import org.checkerframework.framework.flow.CFTransfer;
// import org.checkerframework.framework.flow.CFValue;
// import org.checkerframework.framework.type.AnnotatedTypeFactory;
// import org.checkerframework.framework.type.AnnotatedTypeMirror;
// import org.checkerframework.javacutil.AnnotationBuilder;
// import org.checkerframework.javacutil.AnnotationUtils;
//
// public class SizeOfTransfer extends CFTransfer {
//    /** The SizeOfTypeFactory. */
//    AnnotatedTypeFactory atypeFactory;
//    /** The {@literal @}{@link UnknownSizeOf} annotation. */
//    AnnotationMirror UNKNOWN_SIZEOF;
//    /** The {@literal @}{@link SizeOf} annotation. */
//    AnnotationMirror SIZEOF;
//    /** Processing environment. */
//    ProcessingEnvironment processingEnv;
//
//    public SizeOfTransfer(CFAnalysis analysis) {
//        super(analysis);
//        atypeFactory = analysis.getTypeFactory();
//        processingEnv = atypeFactory.getProcessingEnv();
//        SIZEOF = new AnnotationBuilder(processingEnv, SizeOf.class).build();
//        UNKNOWN_SIZEOF = new AnnotationBuilder(processingEnv, UnknownSizeOf.class).build();
//    }
//
//    @Override
//    public TransferResult<CFValue, CFStore> visitLessThan(
//            LessThanNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput) {
//        TransferResult<CFValue, CFStore> result =
//                super.visitLessThan(n, cfValueCFStoreTransferInput);
//        Node leftOp = n.getLeftOperand();
//        Node rightOp = n.getRightOperand();
//        AnnotatedTypeMirror rightAnno = atypeFactory.getAnnotatedType(rightOp.getTree());
//        if (AnnotationUtils.areSameByClass(rightAnno.getAnnotation(), SizeOf.class)) {
//            Receiver leftRec = FlowExpressions.internalReprOf(atypeFactory, leftOp);
//
//            CFStore thenStore = result.getRegularStore();
//            CFStore elseStore = thenStore.copy();
//            ConditionalTransferResult<CFValue, CFStore> newResult =
//                    new ConditionalTransferResult<>(result.getResultValue(), thenStore,
// elseStore);
//
//            CFValue cfValue = analysis.getValue(rightOp);
//            AnnotationMirror rightAnnotationMirror =
//                    atypeFactory
//                            .getQualifierHierarchy()
//                            .findAnnotationInHierarchy(cfValue.getAnnotations(), UNKNOWN_SIZEOF);
//            thenStore.insertValue(leftRec, rightAnnotationMirror);
//            return newResult;
//        }
//        return result;
//    }
//
//    @Override
//    public TransferResult<CFValue, CFStore> visitGreaterThanOrEqual(
//            GreaterThanOrEqualNode n, TransferInput<CFValue, CFStore> cfValueCFStoreTransferInput)
// {
//        TransferResult<CFValue, CFStore> result =
//                super.visitGreaterThanOrEqual(n, cfValueCFStoreTransferInput);
//        Node leftOp = n.getLeftOperand();
//        Node rightOp = n.getRightOperand();
//        AnnotatedTypeMirror rightAnno = atypeFactory.getAnnotatedType(rightOp.getTree());
//        if (AnnotationUtils.areSameByClass(rightAnno.getAnnotation(), SizeOf.class)) {
//            Receiver leftRec = FlowExpressions.internalReprOf(atypeFactory, leftOp);
//
//            CFStore thenStore = result.getRegularStore();
//            CFStore elseStore = thenStore.copy();
//            ConditionalTransferResult<CFValue, CFStore> newResult =
//                    new ConditionalTransferResult<>(result.getResultValue(), thenStore,
// elseStore);
//
//            CFValue cfValue = analysis.getValue(rightOp);
//            AnnotationMirror rightAnnotationMirror =
//                    atypeFactory
//                            .getQualifierHierarchy()
//                            .findAnnotationInHierarchy(cfValue.getAnnotations(), UNKNOWN_SIZEOF);
//            elseStore.insertValue(leftRec, rightAnnotationMirror);
//            return newResult;
//        }
//        AnnotatedTypeMirror leftAnno = atypeFactory.getAnnotatedType(leftOp.getTree());
//        if (AnnotationUtils.areSameByClass(leftAnno.getAnnotation(), SizeOf.class)) {
//            Receiver rightRec = FlowExpressions.internalReprOf(atypeFactory, rightOp);
//
//            CFStore thenStore = result.getRegularStore();
//            CFStore elseStore = thenStore.copy();
//            ConditionalTransferResult<CFValue, CFStore> newResult =
//                    new ConditionalTransferResult<>(result.getResultValue(), thenStore,
// elseStore);
//
//            CFValue cfValue = analysis.getValue(leftOp);
//            AnnotationMirror leftAnnotationMirror =
//                    atypeFactory
//                            .getQualifierHierarchy()
//                            .findAnnotationInHierarchy(cfValue.getAnnotations(), UNKNOWN_SIZEOF);
//            thenStore.insertValue(rightRec, leftAnnotationMirror);
//            return newResult;
//        }
//        return result;
//    }
// }
