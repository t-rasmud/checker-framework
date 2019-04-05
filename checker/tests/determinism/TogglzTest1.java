import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class TogglzTest1<F> {
    private @OrderNonDet Set<@Det Set<@Det F>> deepCopy(Set<@Det Set<@Det F>> src) {
        @OrderNonDet Set<@Det Set<@Det F>> copy = new HashSet<@Det Set<@Det F>>();
        // :: error: (enhancedfor.type.incompatible)
        for (@Det Set<@Det F> variant : src) {
            // :: error: (argument.type.incompatible) :: error:
            // (invalid.upper.bound.on.type.argument)
            copy.add(new HashSet<F>(variant));
        }
        return copy;
    }
}
