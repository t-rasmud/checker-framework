import org.checkerframework.checker.determinism.qual.*;

public class Issue23Det {

    public static <T> @NonDet String arrToString(T[] arr) {
        return arr.toString();
    }

    public static <T> @NonDet String callToString(T[] arr) {
        return arrToString(arr);
    }

    public static <T> @NonDet String callToStringDet(T @Det [] arr) {
        return arrToString(arr);
    }

    public static <T> @NonDet String callToStringNonDet(T @NonDet [] arr) {
        // :: error: (argument.type.incompatible)
        return arrToString(arr);
    }

    public static @NonDet String arrToString1(@PolyDet Object @PolyDet [] arr) {
        return arr.toString();
    }

    public static <T extends @PolyDet Object> @NonDet String callToString1(T @PolyDet [] arr) {
        return arrToString1(arr);
    }

    public static <T extends @Det Object> @NonDet String callToStringDet1(T @Det [] arr) {
        return arrToString1(arr);
    }

    public static <T extends @NonDet Object> @Det String callToStringDet2(T @Det [] arr) {
        // :: error: (return.type.incompatible)
        return arrToString1(arr);
    }
}
