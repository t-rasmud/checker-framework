import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class PolyUseTest {
    public static void f(@PolyDet String a, @PolyDet("use") String b) {}

    public static void g(@PolyDet List<@PolyDet String> list) {
        f(list.get(0), list.get(0));
    }
}
