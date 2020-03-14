import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class MapEntrySet {
    void testNDMap(@NonDet Map<@NonDet String, @NonDet String> map) {
        @NonDet Set<Map.@NonDet Entry<@NonDet String, @NonDet String>> entries = map.entrySet();
    }

    void testONDMap(@OrderNonDet Map<@Det String, @Det String> map) {
        @OrderNonDet Set<Map.@Det Entry<@Det String, @Det String>> entries = map.entrySet();
    }

    void testDetMap(@Det Map<@Det String, @Det String> map) {
        @Det Set<Map.@Det Entry<@Det String, @Det String>> entries = map.entrySet();
    }
}
