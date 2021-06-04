package nonempty;

import java.util.List;

public class SizeVariable {
  void test1(List<Integer> lst) {
    int size = lst.size();
    if (size == 1) {
      lst.iterator().next();
    }
  }

  void test2(List<Integer> lst) {
    int size = lst.size();
    size += 5;
    if (size == 1) {
      lst.iterator().next();
    }
  }
}
