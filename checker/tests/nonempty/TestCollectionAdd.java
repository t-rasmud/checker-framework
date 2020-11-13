package nonempty;

import org.checkerframework.checker.nonempty.qual.NonEmpty;

import java.util.Collection;
import java.util.List;

public class TestCollectionAdd {
    void test(Collection<Integer> a) {
        a.add(5);
        m(a);
    }
    void m(@NonEmpty Collection<Integer> a) { }

    void listAdd(List<Integer> a) {
        a.add(5);
        m(a);
        a.remove(0);
        // :: error: argument.type.incompatible
        m(a);
    }
}
