import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class TestPolyNoOrderNonDet {
    static @PolyDet int test(@PolyDet("noOrderNonDet") int x) {
        return x;
    }

    @NonDet int caller(@NonDet int x) {
        return test(x);
    }

    @Det int caller1(@NonDet int x) {
        // :: error: (return.type.incompatible)
        return test(x);
    }

    @Det int caller2(@Det int x) {
        return test(x);
    }

    static void testOnd(
            @PolyDet("noOrderNonDet") List<@PolyDet("useNoOrderNonDet") String> lst,
            @PolyDet("useNoOrderNonDet") int index) {}

    void callerLst(@OrderNonDet List<@Det String> lst, @Det int index) {
        // :: error: (argument.type.incompatible)
        testOnd(lst, index);
    }

    void callerPoly(@PolyDet List<@PolyDet String> lst, @Det int index) {
        // :: error: (argument.type.incompatible)
        testOnd(lst, index);
    }

    void callerND(@NonDet List<@NonDet String> lst, @Det int index) {
        testOnd(lst, index);
    }

    void callerD(@Det List<@Det String> lst, @Det int index) {
        testOnd(lst, index);
    }

    void callerD1(@Det List<@Det String> lst, @NonDet int index) {
        // :: error: (argument.type.incompatible)
        testOnd(lst, index);
    }

    static void testList(@PolyDet("noOrderNonDet") List<@PolyDet("useNoOrderNonDet") String> lst) {}

    void callerTestList(
            @OrderNonDet List<@Det String> lst,
            @Det List<@Det String> lst1,
            @NonDet List<@NonDet String> lst2) {
        // :: error: (argument.type.incompatible)
        testList(lst);
        testList(lst1);
        testList(lst2);
    }
}
