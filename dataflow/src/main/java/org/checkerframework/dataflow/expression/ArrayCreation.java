package org.checkerframework.dataflow.expression;

import java.util.List;
import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.plumelib.util.UtilPlume;

/** FlowExpression for array creations. {@code new String[]()}. */
public class ArrayCreation extends Receiver {

    /** List of dimensions expressions. {code null} means that there is no dimension expression. */
    protected final List<? extends @Nullable Receiver> dimensions;
    /** List of initializers. */
    protected final List<Receiver> initializers;

    /**
     * Creates an ArrayCreation object.
     *
     * @param type array type
     * @param dimensions list of dimension expressions; {code null} means that there is no dimension
     *     expression
     * @param initializers list of initializer expressions
     */
    public ArrayCreation(
            TypeMirror type,
            List<? extends @Nullable Receiver> dimensions,
            List<Receiver> initializers) {
        super(type);
        this.dimensions = dimensions;
        this.initializers = initializers;
    }

    /**
     * Returns a list of receivers representing the dimension of this array creation.
     *
     * @return a list of receivers representing the dimension of this array creation
     */
    public @PolyDet List<? extends @Nullable Receiver> getDimensions(@PolyDet ArrayCreation this) {
        return dimensions;
    }

    public @PolyDet List<@Det Receiver> getInitializers(@PolyDet ArrayCreation this) {
        return initializers;
    }

    @Override
    public boolean containsOfClass(Class<? extends Receiver> clazz) {
        for (Receiver n : dimensions) {
            if (n != null && n.getClass() == clazz) {
                return true;
            }
        }
        for (Receiver n : initializers) {
            if (n.getClass() == clazz) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isUnassignableByOtherCode() {
        return false;
    }

    @Override
    public boolean isUnmodifiableByOtherCode() {
        return false;
    }

    @Override
    public @NonDet int hashCode(@PolyDet ArrayCreation this) {
        return Objects.hash(dimensions, initializers, getType().toString());
    }

    @Override
    public @PolyDet("up") boolean equals(
            @PolyDet ArrayCreation this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ArrayCreation)) {
            return false;
        }
        ArrayCreation other = (ArrayCreation) obj;
        return this.dimensions.equals(other.getDimensions())
                && this.initializers.equals(other.getInitializers())
                // It might be better to use Types.isSameType(getType(), other.getType()), but I
                // don't have a Types object.
                && getType().toString().equals(other.getType().toString());
    }

    @Override
    public boolean syntacticEquals(Receiver other) {
        return this.equals(other);
    }

    @Override
    public boolean containsSyntacticEqualReceiver(Receiver other) {
        return syntacticEquals(other);
    }

    @Override
    public @PolyDet String toString(@PolyDet ArrayCreation this) {
        StringBuilder sb = new @PolyDet StringBuilder();
        sb.append("new " + type);
        if (!dimensions.isEmpty()) {
            for (Receiver dim : dimensions) {
                sb.append("[");
                sb.append(dim == null ? "" : dim);
                sb.append("]");
            }
        }
        if (!initializers.isEmpty()) {
            sb.append(" {");
            sb.append(UtilPlume.join(", ", initializers));
            sb.append("}");
        }
        return sb.toString();
    }
}
