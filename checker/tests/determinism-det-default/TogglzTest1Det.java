import java.util.HashSet;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class TogglzTest1Det<F> {
    private @OrderNonDet Set<@Det Set<@Det F>> deepCopy(@PolyDet Set<@Det Set<@Det F>> src) {
        @OrderNonDet Set<@Det Set<@Det F>> copy = new HashSet<@Det Set<@Det F>>();
        // :: error: (enhancedfor.type.incompatible)
        for (@Det Set<@Det F> variant : src) {
            // :: error: (argument.type.incompatible)
            copy.add(new HashSet<F>(variant));
        }
        return copy;
    }
}
