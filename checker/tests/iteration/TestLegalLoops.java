package iteration;

import java.util.ArrayList;
import java.util.Iterator;

public class TestLegalLoops {
    void testWhile(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        while (iter.hasNext()) {
            iter.next();
        }
    }

    void testFor(ArrayList<Integer> lst) {
        for (Iterator<Integer> iter = lst.iterator(); iter.hasNext(); ) {
            iter.next();
        }
    }

    void testForEach(ArrayList<Integer> lst) {
        for (Integer elem : lst) {}
    }
}
