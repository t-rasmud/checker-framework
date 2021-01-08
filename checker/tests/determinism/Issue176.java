import java.util.HashSet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class Issue176 {
    public static void f() {
        @PolyDet("upDet") HashSet<@PolyDet String> set = new @PolyDet("upDet") HashSet<>();
    }
}
