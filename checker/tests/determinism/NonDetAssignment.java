import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;

public @NonDet class NonDetAssignment {
    @NonDet int ndField;
    @Det int detField;

    void test(@NonDet int ndArg, @Det int detArg) {
        this.ndField = ndArg;
        // :: error: (invalid.field.assignment)
        this.detField = detArg;
    }
}
