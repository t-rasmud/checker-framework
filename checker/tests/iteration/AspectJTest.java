package iteration;

import java.util.Iterator;

public class AspectJTest {
  void test(Iterator<Integer> iter, Iterator<Integer> iter2) {
    if (iter.hasNext()) {
      // :: error: method.invocation
      iter2.next();
    }
  }
}
