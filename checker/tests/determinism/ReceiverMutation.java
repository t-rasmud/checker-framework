import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class ReceiverMutation {
    void test(@NonDet ArrayList<@Det String> lst) {
        // :: error: (invalid.mutation)
        lst.add("hello");
    }

    void test1(@OrderNonDet ArrayList<@Det String> lst) {
        lst.add("hello");
    }

    void test2(@OrderNonDet ArrayList<@OrderNonDet ArrayList<@Det String>> lst) {
        @NonDet ArrayList<@Det String> local = lst.get(0);
        // :: error: (invalid.mutation)
        local.add("hello");
    }

    void test3(@PolyDet ArrayList<@PolyDet ArrayList<@PolyDet String>> lst) {
        // :: error: (invalid.mutation)
        lst.get(0).add("hello");
    }
}
