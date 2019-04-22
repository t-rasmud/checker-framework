import org.checkerframework.checker.determinism.qual.*;

public class PolyUpTest {
    @PolyDet("up") int test(@PolyDet Object arg) {
        @PolyDet("up") int x = 5;
        return x;
    }

    // Dummy method that returns @PolyDet("down").
    @PolyDet("down") String retPolyUp(@PolyDet PolyUpTest this) {
        return null;
    }

    // Dummy method that returns @PolyDet("up").
    @PolyDet("up") String retPolyDown(@PolyDet PolyUpTest this) {
        return null;
    }

    // Tests that @PolyDet("up") is converted to @PolyDet.
    @PolyDet String testPolyUpNonCollecstion(@PolyDet PolyUpTest p) {
        return p.retPolyUp();
    }

    // Tests that @PolyDet("down") is not converted to @PolyDet.
    @PolyDet("down") String testPolyDownNonCollection(@PolyDet PolyUpTest p) {
        return p.retPolyDown();
    }
}
