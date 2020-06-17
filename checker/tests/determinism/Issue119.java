import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class Issue119 {
    public static <T extends @PolyDet("use") Object> void formSublists(
            List<T> list, int maxLength) {
        @PolyDet("up") List<@PolyDet("up") List<T>> result = new @PolyDet("up") ArrayList<>();
        // :: error: (argument.type.incompatible)
        @PolyDet("up") List<T> subList = list.subList(maxLength, maxLength + 1);
        result.add(subList);
    }
}
