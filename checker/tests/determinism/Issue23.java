import org.checkerframework.checker.determinism.qual.*;

// @skip-test
public class Issue23 {
    public static <T> String arrToString(T[] arr) {
        return arr.toString();
    }

    public static <T> String callToString(T[] arr) {
        return arrToString(arr);
    }

    public static <T> @Det String callToStringDet(T @Det [] arr) {
        return arrToString(arr);
    }
}
