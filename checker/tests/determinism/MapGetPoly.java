// Test case for https://github.com/t-rasmud/checker-framework/issues/212

// @skip-test until the bug is fixed

import java.util.HashMap;
import java.util.LinkedHashMap;
import org.checkerframework.checker.determinism.qual.*;

public class MapGetPoly {

    private static @Det LinkedHashMap<String, Integer> map1 = new @Det LinkedHashMap<>(8);

    private static @OrderNonDet HashMap<String, Integer> map2 = new @OrderNonDet HashMap<>(8);

    public static @PolyDet Integer myGet1(@PolyDet String key) {
        return map1.get(key);
    }

    public static @PolyDet Integer myGet2(@PolyDet String key) {
        return map2.get(key);
    }
}
