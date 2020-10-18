package org.checkerframework.dataflow.expression;

import java.util.Objects;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.analysis.Store;

/**
 * A ClassName represents the occurrence of a class as part of a static field access or method
 * invocation.
 */
public class ClassName extends Receiver {
    private final String typeString;

    public ClassName(TypeMirror type) {
        super(type);
        typeString = type.toString();
    }

    @Override
    public @PolyDet boolean equals(@PolyDet ClassName this, @PolyDet @Nullable Object obj) {
        if (!(obj instanceof ClassName)) {
            return false;
        }
        ClassName other = (ClassName) obj;
        return typeString.equals(other.typeString);
    }

    @Override
    public @PolyDet int hashCode(@PolyDet ClassName this) {
        return Objects.hash(typeString);
    }

    @Override
    public @PolyDet String toString(@PolyDet ClassName this) {
        return typeString + ".class";
    }

    @Override
    public boolean containsOfClass(Class<? extends Receiver> clazz) {
        return getClass() == clazz;
    }

    @Override
    public boolean syntacticEquals(Receiver other) {
        return this.equals(other);
    }

    @Override
    public boolean isUnassignableByOtherCode() {
        return true;
    }

    @Override
    public boolean isUnmodifiableByOtherCode() {
        return true;
    }

    @Override
    public boolean containsModifiableAliasOf(Store<?> store, Receiver other) {
        return false; // not modifiable
    }
}
