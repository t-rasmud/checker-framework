package iteration;

import java.util.Collection;
import java.util.Iterator;

public class ClosureFP2 {
    void test(Collection<String> coll1, Collection<String> coll2) {
        if (coll1.size() != coll2.size()) {
            return;
        }
        Iterator iter1 = coll1.iterator();
        Iterator iter2 = coll2.iterator();
        while (iter1.hasNext()) {
            // :: error: method.invocation.invalid
            iter2.next();
        }
    }
}
