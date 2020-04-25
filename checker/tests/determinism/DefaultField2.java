import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class DefaultField2 {
    static Object staticField;
    Object field;

    @Det Object getField(@Det DefaultField2 this) {
        return field;
    }

    @PolyDet Object getField2(@PolyDet DefaultField2 this) {
        return field;
    }

    void test(@Det DefaultField2 this) {
        @Det Object o = field;
        @NonDet Object o2 = field;
        @Det Object o3 = staticField;
    }
}
