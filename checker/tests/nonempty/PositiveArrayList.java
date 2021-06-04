package nonempty;

import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

public class PositiveArrayList {
  void test() {
    List<Integer> lst = new ArrayList<Integer>(7);
    lst.get(5);
  }

  void test1(@NonEmpty List<Integer> lst) {
    int size = lst.size();
    new ArrayList<Integer>(size);
    new ArrayList<Integer>(lst.size());
  }
}
