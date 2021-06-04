package nonempty;

import java.util.List;

public class TestSizeComparison {
  void test(List<Integer> lst) {
    if (lst.size() > 0) {
      lst.get(0);
    }
  }
}
