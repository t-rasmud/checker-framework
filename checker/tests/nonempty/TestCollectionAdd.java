package nonempty;

import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

public class TestCollectionAdd {
    void test(Collection<Integer> a) {
        a.add(5);
        m(a);
    }

    void m(@NonEmpty Collection<Integer> a) {}

    void listAdd(List<Integer> a) {
        a.add(5);
        m(a);
        a.remove(0);
        // :: error: argument
        m(a);
    }

    void listAdd1(List<Integer> a) {
        a.add(5);
        m(a);
        // :: error: argument
        m(a);
    }
}
