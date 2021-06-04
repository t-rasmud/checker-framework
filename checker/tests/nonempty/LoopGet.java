package nonempty;

import java.util.List;

public class LoopGet {
  void test(List<String> lst) {
    int size = lst.size();
    for (int i = 0; i < size; i++) {
      lst.get(i);
    }
  }
}
