import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class Issue95 {
    void test(@OrderNonDet List<@Det Class<@Det String>> classList, Object obj) {
        @NonDet Class<@Det String> cl = classList.get(0);
        // :: error: (assignment.type.incompatible)
        @Det Class casted = cl.getClass().cast(obj);
    }
}
