package org.checkerframework.checker.determinism;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import java.util.List;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.FlowExpressions.FieldAccess;
import org.checkerframework.dataflow.analysis.FlowExpressions.Receiver;
import org.checkerframework.dataflow.analysis.FlowExpressions.ThisReference;
import org.checkerframework.dataflow.cfg.UnderlyingAST;
import org.checkerframework.dataflow.cfg.UnderlyingAST.CFGMethod;
import org.checkerframework.dataflow.cfg.UnderlyingAST.Kind;
import org.checkerframework.dataflow.cfg.node.LocalVariableNode;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFStore;
import org.checkerframework.framework.flow.CFTransfer;
import org.checkerframework.framework.flow.CFValue;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TreeUtils;

/**
 * Transfer function for the determinism type-system.
 *
 * <p>Performs type refinement from {@code @OrderNonDet} to {@code @Det} for:
 *
 * <ul>
 *   <li>The receiver of List.sort.
 *   <li>The first argument of Arrays.sort.
 *   <li>The first argument of Arrays.parallelSort.
 *   <li>The first argument of Collections.sort.
 * </ul>
 *
 * <p>Performs type refinement from {@code @Det} to {@code @OrderNonDet} for
 *
 * <ul>
 *   <li>The first argument of Collections.shuffle.
 * </ul>
 */
public class DeterminismTransfer extends CFTransfer {
    /** Calls the superclass constructor. */
    public DeterminismTransfer(CFAbstractAnalysis<CFValue, CFStore, CFTransfer> analysis) {
        super(analysis);
    }

    /**
     * Clear all non-static fields from the initial store.
     *
     * <p>Dataflow assumes that the type of a field access is at most the declared type of the
     * field. This isn't true for the deterministic fields if they are accessed via a
     * non-deterministic expression.
     */
    @Override
    public CFStore initialStore(
            UnderlyingAST underlyingAST, @Nullable List<LocalVariableNode> parameters) {
        CFStore initStore = super.initialStore(underlyingAST, parameters);
        if (underlyingAST.getKind() == Kind.METHOD) {
            CFGMethod method = (CFGMethod) underlyingAST;
            final ClassTree classTree = method.getClassTree();
            TypeMirror classType = TreeUtils.typeOf(classTree);
            Receiver receiver = new ThisReference(classType);
            for (Tree member : classTree.getMembers()) {
                if (member.getKind() == Tree.Kind.VARIABLE) {
                    VariableElement e = TreeUtils.elementFromDeclaration((VariableTree) member);
                    if (!ElementUtils.isStatic(e)) {
                        TypeMirror fieldType = ElementUtils.getType(e);
                        Receiver field = new FieldAccess(receiver, fieldType, e);
                        initStore.clearValue(field);
                    }
                }
            }
        }
        return initStore;
    }
}
