package org.checkerframework.dataflow.expression;

import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;

/** An array access. */
public class ArrayAccess extends Receiver {

    protected final Receiver receiver;
    protected final Receiver index;

    public ArrayAccess(TypeMirror type, Receiver receiver, Receiver index) {
        super(type);
        this.receiver = receiver;
        this.index = index;
    }

    @Override
    public boolean containsOfClass(Class<? extends Receiver> clazz) {
        if (getClass() == clazz) {
            return true;
        }
        if (receiver.containsOfClass(clazz)) {
            return true;
        }
        return index.containsOfClass(clazz);
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public Receiver getIndex() {
        return index;
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
    public boolean containsSyntacticEqualReceiver(Receiver other) {
        return syntacticEquals(other)
                || receiver.syntacticEquals(other)
                || index.syntacticEquals(other);
    }

    @Override
    public boolean syntacticEquals(Receiver other) {
        if (!(other instanceof ArrayAccess)) {
            return false;
        }
        ArrayAccess otherArrayAccess = (ArrayAccess) other;
        if (!receiver.syntacticEquals(otherArrayAccess.receiver)) {
            return false;
        }
        return index.syntacticEquals(otherArrayAccess.index);
    }

    @Override
    public boolean containsModifiableAliasOf(Store<?> store, Receiver other) {
        if (receiver.containsModifiableAliasOf(store, other)) {
            return true;
        }
        return index.containsModifiableAliasOf(store, other);
    }

    @Override
    public @PolyDet boolean equals(@PolyDet ArrayAccess this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ArrayAccess)) {
            return false;
        }
        ArrayAccess other = (ArrayAccess) obj;
        return receiver.equals(other.receiver) && index.equals(other.index);
    }

    @Override
    public @PolyDet int hashCode(@PolyDet ArrayAccess this) {
        return Objects.hash(receiver, index);
    }

    @Override
    public @PolyDet String toString(@PolyDet ArrayAccess this) {
        @PolyDet StringBuilder result = new @PolyDet StringBuilder();
        result.append(receiver.toString());
        result.append("[");
        result.append(index.toString());
        result.append("]");
        return result.toString();
    }
}
