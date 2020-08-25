// @skip-test
import org.checkerframework.checker.determinism.qual.*;

public class Issue191 {
    public @PolyDet("up") boolean f(@PolyDet Object o1, @PolyDet Object o2) {
        return o1.equals(o2);
    }

    public @PolyDet("up") boolean f1(@PolyDet String o1, @PolyDet String o2) {
        return o1.equals(o2);
    }

    public @PolyDet("up") boolean f2(@PolyDet Integer o1, @PolyDet Integer o2) {
        return o1.equals(o2);
    }
}
