import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class TestInvariantCollections {
    void test(@Det List<@Det String> lst, @NonDet int index) {
        // :: error: (method.invocation.invalid)
        @NonDet String element = lst.get(index);
    }

    void test1(@OrderNonDet List<@Det String> lst, @Det List<@Det String> lst1) {
        lst.addAll(lst1);
    }

    void test2(@OrderNonDet List<@Det String> lst, @OrderNonDet List<@Det String> lst1) {
        lst.addAll(lst1);
    }

    void test3(@Det List<@Det String> lst, @NonDet List<@Det String> lst1) {
        // :: error: (method.invocation.invalid)
        @NonDet boolean result = lst.equals(lst1);
    }
}
