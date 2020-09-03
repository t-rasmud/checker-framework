import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class Issue119Det {
    public static <T extends @PolyDet("use") Object> void formSublists(
            @PolyDet List<T> list, @PolyDet int maxLength) {
        @PolyDet("up") List<@PolyDet("up") List<T>> result = new @PolyDet("up") ArrayList<>();
        // :: error: (argument.type.incompatible)
        @PolyDet("up") List<T> subList = list.subList(maxLength, maxLength + 1);
        // :: error: (argument.type.incompatible)
        result.add(subList);
    }
}
