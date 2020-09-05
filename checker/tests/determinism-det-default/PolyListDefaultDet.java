import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class PolyListDefaultDet {
    // :: error: (invalid.element.type)
    public static void f(List<@PolyDet String> list) {
        // :: error: (assignment.type.incompatible)  :: error: (invalid.element.type)
        @Det List<@Det String> copy = list;
    }

    // :: error: (invalid.element.type)
    public static void g(Set<@PolyDet String> set) {
        // :: error: (assignment.type.incompatible) :: error: (invalid.element.type)
        @Det Set<@Det String> copy = set;
    }
}
