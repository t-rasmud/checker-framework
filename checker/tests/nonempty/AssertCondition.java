package nonempty;

import java.util.Collection;

public class AssertCondition {
    void test(Collection<Integer> coll) {
        assert !coll.isEmpty() : "@AssumeAssertion(nonempty)";
        coll.iterator().next();
    }
}
