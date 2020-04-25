import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue58 {
    public Issue58(@PolyDet int a) {}

    public static void f(@PolyDet int b) {
        // :: error: (assignment.type.incompatible)
        @Det Issue58 c = new Issue58(b);
    }
}
