import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class ListGetPolyUp {
    void test(@OrderNonDet List<@Det List<@Det String>> olist) {
        // :: error: (assignment.type.incompatible) :: error: (invalid.element.type)
        @NonDet List<@NonDet String> result = olist.get(0);
    }
}
