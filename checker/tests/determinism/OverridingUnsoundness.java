import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class OverridingUnsoundness {
    void method(@PolyDet String a, @PolyDet("use") String b) {}

    void testMethod(@NonDet String c) {}
}

class OverrideSubClass extends OverridingUnsoundness {
    @Override
    // :: error: (override.param.invalid)
    void method(@PolyDet("use") String a, @PolyDet String b) {}

    @Override
    // :: error: (override.param.invalid)
    void testMethod(@Det String c) {}
}
