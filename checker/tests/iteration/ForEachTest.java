package iteration;

import java.util.Collection;
import java.util.Iterator;

public class ForEachTest {
    void test(Collection<Integer> coll) {
        for (Iterator<Integer> iter = coll.iterator(); iter.hasNext(); iter.next()) {}
        // :: error: method.invocation.invalid
        for (Iterator<Integer> iter = coll.iterator(); ; iter.next()) {}
    }

    void testForEach(Collection<Integer> coll) {
        for (Integer i : coll) {}
    }
}
