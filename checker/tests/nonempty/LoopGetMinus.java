package nonempty;

import java.util.List;

public class LoopGetMinus {
  void test1(List<String> parameters) {
    for (int index = parameters.size() - 1; index >= 0; index--) {
      parameters.get(index);
    }
  }
}
