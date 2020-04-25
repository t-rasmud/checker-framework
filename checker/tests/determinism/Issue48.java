import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue48 {
    static void testParams(@PolyDet Integer a) {}

    static void callTestParams(@PolyDet List<@PolyDet Integer> list) {
        testParams(list.get(0));
    }

    public static void g(@PolyDet Integer @PolyDet [] arr) {
        testParams(arr[0]);
    }
}
