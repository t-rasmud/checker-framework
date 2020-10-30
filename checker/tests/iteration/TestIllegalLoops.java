import java.util.ArrayList;
import java.util.Iterator;

public class TestIllegalLoops {
    void testWhile(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        while (iter.hasNext()) {
            iter.next();
            // :: error: (method.invocation.invalid)
            iter.next();
        }
    }
}
