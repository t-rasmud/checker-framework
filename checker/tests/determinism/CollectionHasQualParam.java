import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class CollectionHasQualParam {
    void test(@OrderNonDet Collection<@Det String> c, @Det Collection<@Det String> d) {
        // :: error: (assignment.type.incompatible)
        c = d;
    }

    void test1(@OrderNonDet List<@Det String> c, @Det List<@Det String> d) {
        // :: error: (assignment.type.incompatible)
        c = d;
    }

    void test2(@OrderNonDet ArrayList<@Det String> c, @Det ArrayList<@Det String> d) {
        // :: error: (assignment.type.incompatible)
        c = d;
    }
}
