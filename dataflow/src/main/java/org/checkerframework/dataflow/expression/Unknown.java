package org.checkerframework.dataflow.expression;

import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.interning.qual.UsesObjectEquals;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;

/** Stands for any expression that the Dataflow Framework lacks explicit support for. */
@UsesObjectEquals
public class Unknown extends Receiver {
    /**
     * Create a new Unknown receiver.
     *
     * @param type the Java type of this receiver
     */
    public Unknown(TypeMirror type) {
        super(type);
    }

    @Override
    public @PolyDet boolean equals(@PolyDet Unknown this, @PolyDet @Nullable Object obj) {
        return obj == this;
    }

    @Override
    public @PolyDet int hashCode(@PolyDet Unknown this) {
        return System.identityHashCode(this);
    }

    @Override
    public @PolyDet String toString(@PolyDet Unknown this) {
        return "?";
    }

    @Override
    public boolean containsModifiableAliasOf(Store<?> store, Receiver other) {
        return true;
    }

    @Override
    public boolean containsOfClass(Class<? extends Receiver> clazz) {
        return getClass() == clazz;
    }

    @Override
    public boolean isUnassignableByOtherCode() {
        return false;
    }

    @Override
    public boolean isUnmodifiableByOtherCode() {
        return false;
    }
}
