import org.checkerframework.checker.determinism.qual.PolyDet;

public class checkConstructorReturn {
    public @PolyDet checkConstructorReturn() {}

    void test() {
        // :: error: (argument.type.incompatible)
        System.out.println(new checkConstructorReturn());
    }
}
