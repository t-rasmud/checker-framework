import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class InvariantListAddAll {

    void test(@OrderNonDet List<@Det String> lst, @OrderNonDet List<@Det String> lst1) {
        lst.addAll(lst1);
    }

    void test1(@OrderNonDet List<@Det String> lst, @Det List<@Det String> lst1) {
        lst.addAll(lst1);
    }
}
