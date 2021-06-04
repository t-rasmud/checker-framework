package iteration;

import java.util.List;

public class TestSizeLub {
  void test(List<Integer> lst, boolean cond) {
    int size = 0;
    if (cond) {
      size = lst.size();
      if (size == 9) {
        lst.iterator().next();
      }
    }
    // :: error: method.invocation
    lst.iterator().next();
  }
}
