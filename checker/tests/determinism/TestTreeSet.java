import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestTreeSet {
    void testNewTreeSet(@OrderNonDet TreeSet<@Det Integer> treeSet) {
        Iterator it = treeSet.iterator();
        while (it.hasNext()) {
            System.out.println((Integer) it.next());
        }
    }

    void testTreeIterator(@OrderNonDet TreeSet<@OrderNonDet TreeSet<@Det String>> treeSet) {
        @Det NavigableSet<@Det TreeSet<@Det String>> nSet = treeSet.descendingSet();
    }

    void testTreeIterator1(
                    @OrderNonDet TreeSet<@OrderNonDet TreeSet<@OrderNonDet TreeSet<@Det Integer>>> treeSet) {
        @Det NavigableSet<@Det TreeSet<@Det TreeSet<@Det Integer>>> nSet = treeSet.descendingSet();
    }

    void testTreeSetEquals(@Det TreeSet<@Det Integer> t, @Det TreeSet<@Det Integer> q) {
        t.equals(q);
    }

    void testTreeSetEquals(@Det HashSet<@Det Integer> t, @Det HashSet<@Det Integer> q) {
        t.equals(q);
    }
}
