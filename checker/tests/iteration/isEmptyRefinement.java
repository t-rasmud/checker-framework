package iteration;

import java.util.List;

public class IsEmptyRefinement {
    void test(List<Integer> lst) {
        if (!lst.isEmpty()) {
            lst.iterator().next();
        }
    }
}
