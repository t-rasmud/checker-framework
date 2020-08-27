package org.checkerframework.dataflow.constantpropagation;

import java.util.Objects;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.AbstractValue;

public class Constant implements AbstractValue<Constant> {

    /** What kind of abstract value is this? */
    protected Type type;

    /** The value of this abstract value (or null). */
    protected @Nullable Integer value;

    public enum Type {
        CONSTANT,
        TOP,
        BOTTOM,
    }

    /** Create a constant for {@code type}. */
    public Constant(Type type) {
        assert type != Type.CONSTANT;
        this.type = type;
    }

    /** Create a constant for {@code value}. */
    public Constant(Integer value) {
        this.type = Type.CONSTANT;
        this.value = value;
    }

    /**
     * Returns whether or not the constant is TOP.
     *
     * @return whether or not the constant is TOP
     */
    public @PolyDet boolean isTop(@PolyDet Constant this) {
        return type == Type.TOP;
    }

    /**
     * Returns whether or not the constant is BOTTOM.
     *
     * @return whether or not the constant is BOTTOM
     */
    public @PolyDet boolean isBottom(@PolyDet Constant this) {
        return type == Type.BOTTOM;
    }

    /**
     * Returns whether or not the constant is CONSTANT.
     *
     * @return whether or not the constant is CONSTANT
     */
    @EnsuresNonNullIf(result = true, expression = "value")
    public @PolyDet boolean isConstant(@PolyDet Constant this) {
        return type == Type.CONSTANT && value != null;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public @PolyDet Integer getValue(@PolyDet Constant this) {
        assert isConstant() : "@AssumeAssertion(nullness): inspection";
        return value;
    }

    @SuppressWarnings(
            "determinism") // valid rule relaxation: copy clearly preserves determinism type, so
    // @PolyDet valid
    public @PolyDet Constant copy(@PolyDet Constant this) {
        if (isConstant()) {
            return new Constant(value);
        }
        return new Constant(type);
    }

    @Override
    public @PolyDet Constant leastUpperBound(@PolyDet Constant this, @PolyDet Constant other) {
        if (other.isBottom()) {
            return this.copy();
        }
        if (this.isBottom()) {
            return other.copy();
        }
        if (other.isTop() || this.isTop()) {
            return new Constant(Type.TOP);
        }
        if (other.getValue().equals(getValue())) {
            return this.copy();
        }
        return new Constant(Type.TOP);
    }

    @Override
    public @PolyDet boolean equals(@PolyDet Constant this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof Constant)) {
            return false;
        }
        Constant other = (Constant) obj;
        return type == other.type && Objects.equals(value, other.value);
    }

    @Override
    public @NonDet int hashCode(@PolyDet Constant this) {
        return Objects.hash(type, value);
    }

    @Override
    @SuppressWarnings("determinism") // calling method on external class requires @Det: Integer
    public @PolyDet String toString(@PolyDet Constant this) {
        switch (type) {
            case TOP:
                return "T";
            case BOTTOM:
                return "-";
            case CONSTANT:
                assert isConstant() : "@AssumeAssertion(nullness)";
                return value.toString();
        }
        assert false;
        return "???";
    }
}
