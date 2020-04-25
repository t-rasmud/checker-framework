import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestMapValidity {
    void testValid1(@Det Map<@Det Integer, @Det String> map) {}

    // :: error: (invalid.element.type)
    void testValid4(@Det Map<@NonDet Integer, @NonDet String> map) {}

    // :: error: (invalid.element.type)
    void testValid7(@NonDet Map<@NonDet Integer, @Det String> map) {}

    void testValid8(@NonDet Map<@NonDet Integer, @NonDet String> map) {}

    // :: error: (invalid.element.type)
    void testValid9(@OrderNonDet Map<@NonDet Integer, @NonDet String> map) {}
}
