import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class Accesses {
    public @Det Object field;
    // :: error: (invalid.polymorphic.qualifier.use)
    public @PolyDet Object polyField;

    @Det Object method1(@PolyDet Accesses this) {
        // :: error: (return.type.incompatible)
        return field;
    }

    @Det Object method2(@Det Accesses this) {
        return field; // ok
    }

    @Override
    public @PolyDet boolean equals(@PolyDet Accesses this, @PolyDet Object o) {
        if (!(o instanceof Accesses)) {
            return false;
        }
        Accesses other = (Accesses) o;
        return this.polyField == other.polyField;
    }

    static class Use {
        void method(@NonDet Accesses nonDet) {
            // :: error: (assignment.type.incompatible)
            @Det Object o1 = nonDet.field; // error
        }
    }
}
