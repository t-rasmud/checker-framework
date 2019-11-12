import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
public class DefaultField {
    Object field;

    @PolyDet Object getField(@PolyDet DefaultField this) {
        return field;
    }

    @PolyDet Object getField2() {
        return field;
    }

    void test() {
        // :: error: (assignment.type.incompatible)
        @Det Object o = field;
        @NonDet Object o2 = field;
    }
}
