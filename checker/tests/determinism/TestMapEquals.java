import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestMapEquals {
    // Test basic cases
    @Det boolean testEquals1(
            @Det Map<@Det String, @Det String> m1, @Det Map<@Det String, @Det String> m2) {
        return m1.equals(m2);
    }

    @Det boolean testEquals2(
            @NonDet Map<@NonDet String, @NonDet String> m1,
            @NonDet Map<@NonDet String, @NonDet String> m2) {
        // :: error: (return.type.incompatible)
        return m1.equals(m2);
    }

    // Test cases where one is @OrderNonDet

    @Det boolean testEqualsOND1(
            @OrderNonDet Map<@Det String, @Det String> m1,
            @OrderNonDet Map<@Det String, @Det String> m2) {
        return m1.equals(m2);
    }

    @Det boolean testEqualsOND2(
            @OrderNonDet Map<@OrderNonDet List<@Det String>, @Det String> m1,
            @OrderNonDet Map<@OrderNonDet List<@Det String>, @Det String> m2) {
        // :: error: (return.type.incompatible)
        return m1.equals(m2);
    }

    @Det boolean testEqualsOND3(
            @OrderNonDet Map<@Det String, @Det String> m1, @Det Map<@Det String, @Det String> m2) {
        // :: error: (return.type.incompatible)
        return m1.equals(m2);
    }

    @Det boolean testEqualsOND4(
            @Det Map<@Det String, @Det String> m1, @OrderNonDet Map<@Det String, @Det String> m2) {
        // :: error: (return.type.incompatible)
        return m1.equals(m2);
    }
}
