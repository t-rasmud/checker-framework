package iteration;

import org.checkerframework.checker.nonempty.qual.NonEmpty;

import java.util.Iterator;

public class TestNonEmptyIterator {
    void test(@NonEmpty Iterator<Integer> a) {
        a.next();
    }
}
