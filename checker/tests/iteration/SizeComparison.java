package iteration;

import java.util.List;

public class SizeComparison {
    void test(List<Integer> lst) {
        if (lst.size() > 0) {
            lst.iterator().next();
        }
    }
}
