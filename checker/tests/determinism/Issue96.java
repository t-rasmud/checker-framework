import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue96 {
    public static @PolyDet boolean f(@PolyDet Issue96 c1, @PolyDet Issue96 c2) {
        return c1.equals(c2);
    }
}
