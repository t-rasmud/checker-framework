import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class Issue119 {
    public static <T extends @PolyDet("use") Object> void formSublists(
            List<T> list, int maxLength) {
        @PolyDet("up") List<@PolyDet("up") List<T>> result = new @PolyDet("up") ArrayList<>();
        @PolyDet("up") List<T> subList = list.subList(maxLength, maxLength + 1);
        result.add(subList);
    }

    public static <T extends @Det Object> void formSublistsDet(
            @Det List<T> list, @Det int maxLength) {
        @Det List<@Det List<T>> result = new @Det ArrayList<>();
        @Det List<T> subList = list.subList(maxLength, maxLength + 1);
        result.add(subList);
    }

    public static <T extends @NonDet Object> void formSublistsNonDet(
            @NonDet List<T> list, @NonDet int maxLength) {
        @NonDet List<@NonDet List<T>> result = new @NonDet ArrayList<>();
        @NonDet List<T> subList = list.subList(maxLength, maxLength + 1);
        result.add(subList);
    }

    public static <T extends @Det Object> void formSublistsOrderNonDet(
            @OrderNonDet List<T> list, @Det int maxLength) {
        @OrderNonDet List<@OrderNonDet List<T>> result = new @OrderNonDet ArrayList<>();
        @OrderNonDet List<T> subList = list.subList(maxLength, maxLength + 1);
        result.add(subList);
    }
}
