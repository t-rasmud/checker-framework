package iteration;

import org.checkerframework.checker.iteration.qual.NonEmpty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class TestInvalidNonEmpty {
    // :: error: (invalid.nonempty)
    void test(@NonEmpty int a) {}

    void test1(@NonEmpty ArrayList<Integer> a) {}

    void test2(@NonEmpty Iterator<Integer> a) {}

    // :: error: (invalid.nonempty)
    void test3(@NonEmpty String a) {}

    // :: error: (invalid.nonempty)
    void test4(int @NonEmpty[] a) {}
}
