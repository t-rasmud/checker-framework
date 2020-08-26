package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.Tree;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a reference to 'this'.
 *
 * <pre>
 *   <em>this</em>
 * </pre>
 */
public class ExplicitThisLiteralNode extends ThisLiteralNode {

    protected final Tree tree;

    public ExplicitThisLiteralNode(Tree t) {
        super(TreeUtils.typeOf(t));
        assert t instanceof IdentifierTree && ((IdentifierTree) t).getName().contentEquals("this");
        tree = t;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet ExplicitThisLiteralNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitExplicitThisLiteral(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet ExplicitThisLiteralNode this) {
        return getName();
    }
}
