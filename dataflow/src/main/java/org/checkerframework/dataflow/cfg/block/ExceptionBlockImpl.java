package org.checkerframework.dataflow.cfg.block;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.javacutil.BugInCF;

/** Base class of the {@link Block} implementation hierarchy. */
public class ExceptionBlockImpl extends SingleSuccessorBlockImpl implements ExceptionBlock {

    /** The node of this block. */
    protected @Nullable Node node;

    /** Set of exceptional successors. */
    protected final Map<TypeMirror, Set<Block>> exceptionalSuccessors;

    /** Create an empty exceptional block. */
    public ExceptionBlockImpl() {
        super(BlockType.EXCEPTION_BLOCK);
        exceptionalSuccessors = new LinkedHashMap<>();
    }

    /** Set the node. */
    public void setNode(Node c) {
        node = c;
        c.setBlock(this);
    }

    @Override
    public Node getNode(ExceptionBlockImpl this) {
        if (node == null) {
            throw new BugInCF("Requested node for exception block before initialization");
        }
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns a singleton list.
     */
    @Override
    public List<@Det Node> getNodes(ExceptionBlockImpl this) {
        return Collections.singletonList(getNode());
    }

    @Override
    public @Nullable Node getLastNode(ExceptionBlockImpl this) {
        return null;
    }

    /**
     * Add an exceptional successor.
     *
     * @param b the successor
     * @param cause the exception type that leads to the given block
     */
    public void addExceptionalSuccessor(BlockImpl b, TypeMirror cause) {
        Set<@Det Block> blocks = exceptionalSuccessors.get(cause);
        if (blocks == null) {
            blocks = new LinkedHashSet<>();
            exceptionalSuccessors.put(cause, blocks);
        }
        blocks.add(b);
        b.addPredecessor(this);
    }

    @Override
    public Map<@Det TypeMirror, Set<@Det Block>> getExceptionalSuccessors(ExceptionBlockImpl this) {
        if (exceptionalSuccessors == null) {
            Map<@Det TypeMirror, @Det Set<@Det Block>> result = Collections.emptyMap();
            return result;
        }
        return Collections.unmodifiableMap(exceptionalSuccessors);
    }

    @Override
    public Set<@Det Block> getSuccessors(ExceptionBlockImpl this) {
        Set<@Det Block> result = new LinkedHashSet<>(super.getSuccessors());
        for (@Det Set<? extends Block> blocks : getExceptionalSuccessors().values()) {
            result.addAll(blocks);
        }
        return result;
    }

    @Override
    public @PolyDet String toString(@PolyDet ExceptionBlockImpl this) {
        return "ExceptionBlock(" + node + ")";
    }
}
