package iteration;

import java.util.Iterator;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

public class TestNonEmptyIterator {
    void test(@NonEmpty Iterator<Integer> a) {
        a.next();
    }
}
