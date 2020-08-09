import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class ObjectsEquals {
    void test(@OrderNonDet Set<@Det Integer> a, @OrderNonDet Set<@Det Integer> b) {
        @Det boolean res = Objects.equals(a, b);
    }
}
