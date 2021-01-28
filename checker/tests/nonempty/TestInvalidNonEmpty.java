package nonempty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

public class TestInvalidNonEmpty {
    // :: error: (invalid.nonempty)
    void test(@NonEmpty int a) {}

    void test1(@NonEmpty ArrayList<Integer> a) {}

    void test2(@NonEmpty Iterator<Integer> a) {}

    // :: error: (invalid.nonempty)
    void test3(@NonEmpty String a) {}

    void test5(@NonEmpty Map<Integer, Integer> a) {}
}
