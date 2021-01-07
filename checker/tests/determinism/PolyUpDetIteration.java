// @skip-test
import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class PolyUpDetIteration {
    void f(@PolyDet List<@PolyDet String> list) {
        @PolyDet("upDet") Set<@PolyDet String> set = new HashSet<>(list);
        for (String elt : set) {
            // :: error: (assignment.type.incompatible)
            @PolyDet("up") String s = elt;
        }
    }
}
