// @skip-test
import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue197 {
    public @Det Integer getDet(
            @OrderNonDet HashMap<@Det String, @Det Integer> map, @Det String key) {
        return map.get(key);
    }

    public @NonDet Integer getNonDet(
            @OrderNonDet HashMap<@Det String, @Det Integer> map, @NonDet String key) {
        return map.get(key);
    }

    public @PolyDet Integer getPolyDet(
            @OrderNonDet HashMap<@Det String, @Det Integer> map, @PolyDet String key) {
        return map.get(key);
    }

    public @NonDet String getNonDet(@OrderNonDet List<@Det String> map, @Det int key) {
        return map.get(key);
    }
}
