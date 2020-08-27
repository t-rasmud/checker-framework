package org.checkerframework.dataflow.cfg.node;

import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.lang.model.element.ExecutableElement;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.javacutil.TreeUtils;

/**
 * A node for a method access, including a method accesses:
 *
 * <pre>
 *   <em>expression</em> . <em>method</em> ()
 * </pre>
 */
public class MethodAccessNode extends Node {

    protected final ExpressionTree tree;
    protected final ExecutableElement method;
    protected final Node receiver;

    // TODO: add method to get modifiers (static, access level, ..)

    public MethodAccessNode(ExpressionTree tree, Node receiver) {
        super(TreeUtils.typeOf(tree));
        assert TreeUtils.isMethodAccess(tree);
        this.tree = tree;
        assert TreeUtils.isUseOfElement(tree) : "@AssumeAssertion(nullness): tree kind";
        this.method = (ExecutableElement) TreeUtils.elementFromUse(tree);
        this.receiver = receiver;
    }

    public @PolyDet ExecutableElement getMethod(@PolyDet MethodAccessNode this) {
        return method;
    }

    public @PolyDet Node getReceiver(@PolyDet MethodAccessNode this) {
        return receiver;
    }

    @Override
    public @PolyDet Tree getTree(@PolyDet MethodAccessNode this) {
        return tree;
    }

    @Override
    public <R, P> R accept(NodeVisitor<R, P> visitor, P p) {
        return visitor.visitMethodAccess(this, p);
    }

    @Override
    public @PolyDet String toString(@PolyDet MethodAccessNode this) {
        return getReceiver() + "." + method.getSimpleName();
    }

    @Override
    public @PolyDet boolean equals(@PolyDet MethodAccessNode this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof MethodAccessNode)) {
            return false;
        }
        MethodAccessNode other = (MethodAccessNode) obj;
        return getReceiver().equals(other.getReceiver()) && getMethod().equals(other.getMethod());
    }

    @Override
    public @NonDet int hashCode(@PolyDet MethodAccessNode this) {
        return Objects.hash(getReceiver(), getMethod());
    }

    @Override
    public Collection<Node> getOperands() {
        return Collections.singletonList(receiver);
    }
}
