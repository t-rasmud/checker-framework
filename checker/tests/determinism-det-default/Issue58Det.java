import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue58Det {
    public @PolyDet Issue58Det(@PolyDet int a) {}

    public static void f(@PolyDet int b) {
        // :: error: (assignment.type.incompatible)
        @Det Issue58Det c = new Issue58Det(b);
    }
}
