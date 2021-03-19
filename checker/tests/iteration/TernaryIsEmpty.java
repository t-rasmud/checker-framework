package iteration;

import java.util.Collection;

public class TernaryIsEmpty {
    void test(Collection<Integer> coll) {
        Integer i = coll.isEmpty() == true ? 0 : coll.iterator().next();
    }
}
