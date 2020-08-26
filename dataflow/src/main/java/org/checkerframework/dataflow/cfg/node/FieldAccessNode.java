package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.element.VariableElement;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.ElementUtils;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a field access, including a method accesses:
 *
 * <pre>
 *   <em>expression</em> . <em>field</em>
 * </pre>
 */
public class FieldAccessNode extends Node {

    protected final Tree tree;
    protected final VariableElement element;
    protected final String field;
    protected final Node receiver;

    // TODO: add method to get modifiers (static, access level, ..)

    public FieldAccessNode(Tree tree, Node receiver) {
        super(TreeUtils.typeOf(tree));
        assert TreeUtils.isFieldAccess(tree);
        this.tree = tree;
        this.receiver = receiver;
        this.field = TreeUtils.getFieldName(tree);

        if (tree instanceof MemberSelectTree) {
            MemberSelectTree mstree = (MemberSelectTree) tree;
            assert TreeUtils.isUseOfElement(mstree) : "@AssumeAssertion(nullness): tree kind";
            this.element = (VariableElement) TreeUtils.elementFromUse(mstree);
        } else {
            assert tree instanceof IdentifierTree;
            IdentifierTree itree = (IdentifierTree) tree;
            assert TreeUtils.isUseOfElement(itree) : "@AssumeAssertion(nullness): tree kind";
            this.element = (VariableElement) TreeUtils.elementFromUse(itree);
        }
    }

    @SuppressWarnings("determinism") // imprecise library annotation: trees
    public FieldAccessNode(Tree tree, VariableElement element, Node receiver) {
        super(element.asType());
        this.tree = tree;
        this.element = element;
        this.receiver = receiver;
        this.field = element.getSimpleName().toString();
    }

    public VariableElement getElement() {
        return element;
    }

    public @PolyDet Node getReceiver(@PolyDet FieldAccessNode this) {
        return receiver;
    }

    public @PolyDet String getFieldName(@PolyDet FieldAccessNode this) {
        return field;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet FieldAccessNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitFieldAccess(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet FieldAccessNode this) {
        return getReceiver() + "." + field;
    }

    /** Is this a static field? */
    public boolean isStatic() {
        return ElementUtils.isStatic(getElement());
    }

    @Override
    public @PolyDet boolean equals(@PolyDet FieldAccessNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof FieldAccessNode)) {
            return false;
        }
        FieldAccessNode other = (FieldAccessNode) obj;
        return getReceiver().equals(other.getReceiver())
                && getFieldName().equals(other.getFieldName());
    }

    @Override
    public @NonDet int hashCode(@PolyDet FieldAccessNode this) {
        return Objects.hash(getReceiver(), getFieldName());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.singletonList(receiver);
    }
}
