import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class CheckConstructorReturn {
    @NonDet Object nonDetField;
    @Det Object detField;

    public @PolyDet CheckConstructorReturn() {}
    // :: warning: (inconsistent.constructor.type)
    @Det CheckConstructorReturn(@NonDet Object nonDet, @Det Object det) {
        nonDetField = nonDet;
        detField = det;
    }

    void test() {
        // :: error: (argument.type.incompatible)
        System.out.println(new CheckConstructorReturn());
    }

    void test1(@NonDet Object nd, @Det Object d) {
        CheckConstructorReturn x = new CheckConstructorReturn(nd, d);
        System.out.println(x);
    }
}
