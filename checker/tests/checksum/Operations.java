import org.checkerframework.checker.checksum.qual.ChecksummedBy;
import org.checkerframework.checker.checksum.qual.NotChecksummed;

public class Operations {
    void testBinary(@ChecksummedBy("x") int a, @ChecksummedBy("x") int b) {
        // :: error: (invalid.operation)
        int c = a + b;
    }

    void testBinary1(@ChecksummedBy("x") int a, @NotChecksummed int b) {
        // :: error: (invalid.operation)
        int c = a + b;
    }

    void testUnary(@ChecksummedBy("x") int a) {
        // :: error: (invalid.operation)
        a++;
    }

    void testPrint(@ChecksummedBy("x") int a) {
        // :: error: (argument.type.incompatible)
        System.out.println(a);
    }
}
