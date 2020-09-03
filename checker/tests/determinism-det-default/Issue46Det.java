import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue46Det {
    // :: error: (invalid.upper.bound.on.type.argument)
    public static void f(List<? extends @PolyDet("use") Object> a) {}
}
