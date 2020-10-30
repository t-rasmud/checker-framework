import java.util.ArrayList;
import java.util.Iterator;

public class TestIllegal {
    void test(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        // :: error: (method.invocation.invalid)
        iter.next();
    }

    void test1(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        if (!iter.hasNext()) {
            // :: error: (method.invocation.invalid)
            iter.next();
            // :: error: (method.invocation.invalid)
            iter.next();
        }
    }

    void test3(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        if (iter.hasNext()) {
            iter.next();
            // :: error: (method.invocation.invalid)
            iter.next();
        }
    }

    void test4(Iterator<Integer> iter) {
        if (iter.hasNext()) {
            iter.next();
            // :: error: (method.invocation.invalid)
            iter.next();
        }
    }
}
