import org.checkerframework.checker.checksum.qual.*;

public class SubtypingCheck {
    void test(@ChecksummedBy("b") String a, int b) {
        // :: error: (assignment.type.incompatible)
        @NotChecksummed String c = a;
    }
}
