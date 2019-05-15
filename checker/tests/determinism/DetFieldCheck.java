import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;

@NonDet public class DetFieldCheck {
    // :: error: (invalid.annotation.on.field)
    @Det int x;
}
