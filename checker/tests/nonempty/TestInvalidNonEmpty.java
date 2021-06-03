package nonempty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

public class TestInvalidNonEmpty {
    // :: error: annotations.on.use
    void test(@NonEmpty int a) {}

    void test1(@NonEmpty ArrayList<Integer> a) {}

    void test2(@NonEmpty Iterator<Integer> a) {}

    // :: error: annotations.on.use
    void test3(@NonEmpty String a) {}

    void test5(@NonEmpty Map<Integer, Integer> a) {}
}
