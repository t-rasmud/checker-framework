import org.checkerframework.checker.determinism.qual.*;

public class Issue52<T> {
    // :: error: (invalid.array.component.type)
    void testAccess(@Det int @NonDet [] @Det [] arr) {
        // :: error: (assignment.type.incompatible)
        @Det int x = arr[0][0];
    }
}
