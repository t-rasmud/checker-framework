package iteration;

import java.util.Collection;
import java.util.Iterator;

public class CheckAssert {
    void test(Collection<Integer> coll) {
        Iterator<Integer> iter = coll.iterator();
        assert !iter.hasNext() : "@AssumeAssertion(iteration)";
        // :: error: method.invocation.invalid
        iter.next();
    }

    void test1(Collection<Integer> coll) {
        Iterator<Integer> iter = coll.iterator();
        assert iter.hasNext() : "@AssumeAssertion(iteration)";
        iter.next();
    }

    void test2(Collection<Integer> coll) {
        assert !coll.isEmpty() : "@AssumeAssertion(nonempty)";
        coll.iterator().next();
    }
}
