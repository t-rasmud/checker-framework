import org.checkerframework.checker.determinism.qual.*;

public class Issue191 {
    public @PolyDet("up") boolean f(@PolyDet Object o1, @PolyDet Object o2) {
        return o1.equals(o2);
    }
}
