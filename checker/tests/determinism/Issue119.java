import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue119 {
    public static <T extends @PolyDet("use") Object> void f() {
        @PolyDet("up") List<@PolyDet("up") List<T>> result =
                new @PolyDet("up") ArrayList<@PolyDet("up") List<T>>();
        @PolyDet List<T> subList = new @PolyDet ArrayList<>();
        result.add(subList);
    }
}
