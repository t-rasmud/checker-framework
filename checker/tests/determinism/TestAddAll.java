import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class TestAddAll {
    void test(
            @OrderNonDet List<@Det Integer> lst,
            @Det List<@Det Integer> lst1,
            @OrderNonDet List<@Det Integer> lst2,
            @NonDet List<@Det Integer> lst3) {
        lst.addAll(lst1);
        lst.addAll(lst2);
        lst.addAll(lst3);
    }
}
