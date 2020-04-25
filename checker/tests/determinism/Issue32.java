import org.checkerframework.checker.determinism.qual.*;

public class Issue32 {
    void Issue32(@PolyDet Object input) {
        @PolyDet Object local = new @PolyDet Object();
        // :: error: (argument.type.incompatible)
        System.out.println(local);
        // :: error: (argument.type.incompatible)
        System.out.println(input);
    }
}
