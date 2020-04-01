import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class PolyListDefault {
    public static void f(List<@PolyDet String> list) {
        // :: error: (assignment.type.incompatible)
        @Det List<@Det String> copy = list;
    }
}
