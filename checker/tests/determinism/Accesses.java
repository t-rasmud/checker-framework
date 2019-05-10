import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class Accesses {
    public @Det Object field;

    @Det Object method1(@PolyDet Accesses this) {
        // :: error: (return.type.incompatible)
        return field;
    }

    @Det Object method2(@Det Accesses this) {
        return field; // ok
    }

    static class Use {
        void method(@NonDet Accesses nonDet) {
            // :: error: (assignment.type.incompatible)
            @Det Object o1 = nonDet.field; // error
        }
    }
}
