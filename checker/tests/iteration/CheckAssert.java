package iteration;

import java.util.Collection;
import java.util.Iterator;

public class CheckAssert {
  void test(Collection<Integer> coll) {
    Iterator<Integer> iter = coll.iterator();
    assert !iter.hasNext();
    // :: error: method.invocation
    iter.next();
  }

  void test1(Collection<Integer> coll) {
    Iterator<Integer> iter = coll.iterator();
    assert iter.hasNext();
    iter.next();
  }

  void test2(Collection<Integer> coll) {
    assert !coll.isEmpty();
    coll.iterator().next();
  }
}
