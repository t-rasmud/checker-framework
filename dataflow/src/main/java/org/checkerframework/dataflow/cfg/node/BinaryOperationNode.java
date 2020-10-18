package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.BinaryTree;
import java.util.ArrayList;
import java.util.Collection;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a binary expression.
 *
 * <p>For example:
 *
 * <pre>
 *   <em>lefOperandNode</em> <em>operator</em> <em>rightOperandNode</em>
 * </pre>
 */
public abstract class BinaryOperationNode extends Node {

    protected final BinaryTree tree;
    protected final Node left;
    protected final Node right;

    protected BinaryOperationNode(BinaryTree tree, Node left, Node right) {
        super(TreeUtils.typeOf(tree));
        this.tree = tree;
        this.left = left;
        this.right = right;
    }

    public @PolyDet Node getLeftOperand(@PolyDet BinaryOperationNode this) {
        return left;
    }

    public @PolyDet Node getRightOperand(@PolyDet BinaryOperationNode this) {
        return right;
    }

    @Override
    public @PolyDet BinaryTree getTree(@PolyDet BinaryOperationNode this) {
        return tree;
    }

    @Override
    public Collection<Node> getOperands() {
        ArrayList<Node> list = new ArrayList<>(2);
        list.add(getLeftOperand());
        list.add(getRightOperand());
        return list;
    }
}
