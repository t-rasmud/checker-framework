package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node representing a class name used in an expression such as a static method invocation.
 *
 * <p>parent.<em>class</em> .forName(...)
 */
public class ClassNameNode extends Node {

    /** The tree for this node. */
    protected final @Nullable Tree tree;

    /** The class named by this node. */
    protected final Element element;

    /** The parent name, if any. */
    protected final @Nullable Node parent;

    public ClassNameNode(IdentifierTree tree) {
        super(TreeUtils.typeOf(tree));
        assert tree.getKind() == Tree.Kind.IDENTIFIER;
        this.tree = tree;
        assert TreeUtils.isUseOfElement(tree) : "@AssumeAssertion(nullness): tree kind";
        this.element = TreeUtils.elementFromUse(tree);
        this.parent = null;
    }

    /**
     * Create a new ClassNameNode.
     *
     * @param tree the class tree for this node
     */
    public ClassNameNode(ClassTree tree) {
        super(TreeUtils.typeOf(tree));
        this.tree = tree;
        this.element = TreeUtils.elementFromDeclaration(tree);
        this.parent = null;
    }

    public ClassNameNode(MemberSelectTree tree, Node parent) {
        super(TreeUtils.typeOf(tree));
        this.tree = tree;
        assert TreeUtils.isUseOfElement(tree) : "@AssumeAssertion(nullness): tree kind";
        this.element = TreeUtils.elementFromUse(tree);
        this.parent = parent;
    }

    public ClassNameNode(TypeMirror type, Element element) {
        super(type);
        this.tree = null;
        this.element = element;
        this.parent = null;
    }

    public @PolyDet Element getElement(@PolyDet ClassNameNode this) {
        return element;
    }

    /** The parent node of the current node. */
    public @PolyDet @Nullable Node getParent(@PolyDet ClassNameNode this) {
        return parent;
    }

    @Override
    public @PolyDet @Nullable Tree getTree(@PolyDet ClassNameNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitClassName(this, p);
    }

    @Override
    @SuppressWarnings("determinism") // imprecise library annotation: elements
    public @PolyDet String toString(@PolyDet ClassNameNode this) {
        return getElement().getSimpleName().toString();
    }

    @Override
    @SuppressWarnings("determinism") // calling method on external class requires @Det
    public @PolyDet boolean equals(@PolyDet ClassNameNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ClassNameNode)) {
            return false;
        }
        ClassNameNode other = (ClassNameNode) obj;
        return Objects.equals(getParent(), other.getParent())
                && getElement().equals(other.getElement());
    }

    @Override
    public @NonDet int hashCode(@PolyDet ClassNameNode this) {
        return Objects.hash(getElement(), getParent());
    }

    @Override
    public Collection<Node> getOperands() {
        if (parent == null) {
            return Collections.emptyList();
        }
        return Collections.singleton(parent);
    }
}
