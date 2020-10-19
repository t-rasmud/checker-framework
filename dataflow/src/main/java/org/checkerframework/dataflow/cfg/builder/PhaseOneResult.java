package org.checkerframework.dataflow.cfg.builder;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.LambdaExpressionTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.UnaryTree;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.dataflow.cfg.UnderlyingAST;
import org.checkerframework.dataflow.cfg.builder.ExtendedNode.ExtendedNodeType;
import org.checkerframework.dataflow.cfg.node.AssignmentNode;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.dataflow.cfg.node.ReturnNode;

/* --------------------------------------------------------- */
/* Phase One */
/* --------------------------------------------------------- */

/**
 * A wrapper object to pass around the result of phase one. For a documentation of the fields see
 * {@link CFGTranslationPhaseOne}.
 */
public class PhaseOneResult {

    final @OrderNonDet IdentityHashMap<Tree, @OrderNonDet Set<Node>> treeLookupMap;
    final @OrderNonDet IdentityHashMap<Tree, @OrderNonDet Set<Node>> convertedTreeLookupMap;
    final @OrderNonDet IdentityHashMap<UnaryTree, AssignmentNode> unaryAssignNodeLookupMap;
    final UnderlyingAST underlyingAST;
    final @OrderNonDet Map<Label, Integer> bindings;
    final ArrayList<ExtendedNode> nodeList;
    final @OrderNonDet Set<Integer> leaders;
    final List<ReturnNode> returnNodes;
    final Label regularExitLabel;
    final Label exceptionalExitLabel;
    final List<ClassTree> declaredClasses;
    final List<LambdaExpressionTree> declaredLambdas;

    public PhaseOneResult(
            UnderlyingAST underlyingAST,
            @OrderNonDet IdentityHashMap<Tree, @OrderNonDet Set<Node>> treeLookupMap,
            @OrderNonDet IdentityHashMap<Tree, @OrderNonDet Set<Node>> convertedTreeLookupMap,
            @OrderNonDet IdentityHashMap<UnaryTree, AssignmentNode> unaryAssignNodeLookupMap,
            ArrayList<ExtendedNode> nodeList,
            @OrderNonDet Map<Label, Integer> bindings,
            @OrderNonDet Set<Integer> leaders,
            List<ReturnNode> returnNodes,
            Label regularExitLabel,
            Label exceptionalExitLabel,
            List<ClassTree> declaredClasses,
            List<LambdaExpressionTree> declaredLambdas) {
        this.underlyingAST = underlyingAST;
        this.treeLookupMap = treeLookupMap;
        this.convertedTreeLookupMap = convertedTreeLookupMap;
        this.unaryAssignNodeLookupMap = unaryAssignNodeLookupMap;
        this.nodeList = nodeList;
        this.bindings = bindings;
        this.leaders = leaders;
        this.returnNodes = returnNodes;
        this.regularExitLabel = regularExitLabel;
        this.exceptionalExitLabel = exceptionalExitLabel;
        this.declaredClasses = declaredClasses;
        this.declaredLambdas = declaredLambdas;
    }

    @Override
    @SuppressWarnings("determinism:override.receiver.invalid") // toString only on @Det
    public String toString() {
        StringJoiner sj = new StringJoiner(System.lineSeparator());
        for (ExtendedNode n : nodeList) {
            sj.add(nodeToString(n));
        }
        return sj.toString();
    }

    protected String nodeToString(PhaseOneResult this, @PolyDet ExtendedNode n) {
        if (n.getType() == ExtendedNodeType.CONDITIONAL_JUMP) {
            ConditionalJump t = (ConditionalJump) n;
            return "TwoTargetConditionalJump("
                    + resolveLabel(t.getThenLabel())
                    + ", "
                    + resolveLabel(t.getElseLabel())
                    + ")";
        } else if (n.getType() == ExtendedNodeType.UNCONDITIONAL_JUMP) {
            return "UnconditionalJump(" + resolveLabel(n.getLabel()) + ")";
        } else {
            return n.toString();
        }
    }

    private @PolyDet String resolveLabel(@PolyDet Label label) {
        Integer index = bindings.get(label);
        if (index == null) {
            return "unbound label: " + label;
        }
        return nodeToString(nodeList.get(index));
    }

    /**
     * Returns a verbose string representation of this, useful for debugging.
     *
     * @return a string representation of this
     */
    @SuppressWarnings("determinism") // https://github.com/t-rasmud/checker-framework/issues/194
    public @PolyDet String toStringDebug() {
        StringJoiner result =
                new
                @NonDet StringJoiner(
                        String.format("%n  "),
                        String.format("PhaseOneResult{%n  "),
                        String.format("%n  }"));
        result.add("treeLookupMap=" + treeLookupMap);
        result.add("convertedTreeLookupMap=" + convertedTreeLookupMap);
        result.add("unaryAssignNodeLookupMap=" + unaryAssignNodeLookupMap);
        result.add("underlyingAST=" + underlyingAST);
        result.add("bindings=" + bindings);
        result.add("nodeList=" + CFGBuilder.extendedNodeCollectionToStringDebug(nodeList));
        result.add("leaders=" + leaders);
        result.add("returnNodes=" + Node.nodeCollectionToString(returnNodes));
        result.add("regularExitLabel=" + regularExitLabel);
        result.add("exceptionalExitLabel=" + exceptionalExitLabel);
        result.add("declaredClasses=" + declaredClasses);
        result.add("declaredLambdas=" + declaredLambdas);
        return result.toString();
    }
}
