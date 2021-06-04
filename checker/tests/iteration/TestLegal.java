package iteration;

import java.util.ArrayList;
import java.util.Iterator;

public class TestLegal {
  void test(ArrayList<Integer> lst) {
    Iterator<Integer> iter = lst.iterator();
    if (iter.hasNext()) {
      iter.next();
    }
  }

  void test1(ArrayList<Integer> lst) {
    Iterator<Integer> iter = lst.iterator();
    if (iter.hasNext()) {
      iter.next();
    }
    if (iter.hasNext()) {
      iter.next();
    }
  }
}
