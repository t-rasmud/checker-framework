package iteration;

import java.util.List;

public class isEmptyRefinement {
    void test(List<Integer> lst) {
        if (!lst.isEmpty()) {
            lst.iterator().next();
        }
    }
}
