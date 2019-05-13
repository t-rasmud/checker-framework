// @skip-test until https://github.com/t-rasmud/checker-framework/issues/58 is fixed.
import org.checkerframework.checker.determinism.qual.*;

public class Issue58 {
    public Issue58(@PolyDet int a) {}

    public static void f(@PolyDet int b) {
        @Det Issue58 c = new Issue58(b);
    }
}
