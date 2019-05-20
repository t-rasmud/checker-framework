import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class PolyAssignment {
    @NonDet Object ndField;
    @PolyDet Object polyField;
    @Det Object detField;

    void test(@NonDet Object ndArg, @Det Object detArg) {
        @Det PolyAssignment dObj = new @Det PolyAssignment();
        dObj.ndField = ndArg;
        // :: error: (assignment.type.incompatible)
        dObj.polyField = ndArg;
        dObj.polyField = detArg;
        // :: error: (assignment.type.incompatible)
        dObj.detField = ndArg;
        dObj.detField = detArg;

        @NonDet PolyAssignment ndObj = new @NonDet PolyAssignment();
        ndObj.ndField = ndArg;
        ndObj.polyField = ndArg;
        ndObj.polyField = detArg;
        // :: error: (invalid.field.assignment)
        ndObj.detField = ndArg;
        // :: error: (invalid.field.assignment)
        ndObj.detField = detArg;
    }
}
