import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class InvariantSetAccess {
    void test(@OrderNonDet HashSet<@Det ArrayList<@Det String>> st) {
        @NonDet ArrayList<@Det String> lst = st.iterator().next();
        // :: error: (assignment.type.incompatible)
        @Det String s = lst.get(0);
    }

    void test1(
            @OrderNonDet ArrayList<@Det ArrayList<@Det String>> lst,
            @Det ArrayList<@Det String> lst1,
            @NonDet int index) {
        lst.add(lst1);
        @NonDet ArrayList<@Det String> tmp = lst.get(0);
        tmp.add(index, "");
        // :: error: (argument.type.incompatible)
        lst1.add(index, "");
    }

    static <T> void method(@PolyDet List<@PolyDet("down") T> arg) {}

    void testCall(@NonDet List<@NonDet String> arg1, @NonDet List<@Det String> arg2) {
        method(arg1);
        // :: error: (argument.type.incompatible)
        method(arg2);
    }

    void testCallInvariant(
            @NonDet TestInvariantClass<@NonDet String> arg1,
            @NonDet TestInvariantClass<@Det String> arg2) {
        arg1.test();
        // :: error: (method.invocation.invalid)
        arg2.test();
    }

    void testCallOND(@OrderNonDet List<@Det String> arg1) {
        method(arg1);
    }

    void testPolyCall(@PolyDet List<@PolyDet List<@PolyDet("use") String>> arg) {
        // :: error: (argument.type.incompatible)
        method(arg.get(0));
    }
}

class TestInvariantClass<T> {
    void test(@PolyDet TestInvariantClass<@PolyDet("down") T> this) {}
}
