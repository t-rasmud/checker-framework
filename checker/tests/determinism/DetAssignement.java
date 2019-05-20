import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;

public @Det class DetAssignement {
    @NonDet Object ndField;
    @Det Object detField;

    void test(@NonDet Object ndArg, @Det Object detArg) {
        @Det DetAssignement obj = new @Det DetAssignement();
        obj.ndField = ndArg;
        obj.detField = detArg;
        // :: error: (assignment.type.incompatible)
        obj.detField = ndArg;
    }
}
