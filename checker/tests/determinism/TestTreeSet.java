import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestTreeSet {
    // :: error: (invalid.treeset.or.treemap)
    void testNewTreeSet(@OrderNonDet TreeSet<@Det Integer> treeSet) {}
}
