import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class ObjectsEquals {
    private @OrderNonDet Set<ObjectsEquals> filters = new HashSet<>();

    void test(@OrderNonDet Set<@Det Integer> a, @OrderNonDet Set<@Det Integer> b) {
        @Det boolean res = Objects.equals(a, b);
    }

    @Override
    // :: error: (override.param.invalid)  :: error: (override.receiver.invalid)
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final @Det ObjectsEquals suppressionXpathFilter = (ObjectsEquals) obj;
        return Objects.equals(filters, suppressionXpathFilter.filters);
    }
}
