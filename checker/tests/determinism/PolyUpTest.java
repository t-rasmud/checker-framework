import org.checkerframework.checker.determinism.qual.*;

public class PolyUpTest {
    @PolyDet("up") int test(@PolyDet Object arg) {
        @PolyDet("up") int x = 5;
        return x;
    }

    // Dummy method that returns @PolyDet("up").
    @PolyDet("up") String retPolyUp(@PolyDet PolyUpTest this) {
        return null;
    }

    // Static version.
    static @PolyDet("up") String retPolyUpStatic(@PolyDet PolyUpTest p) {
        return null;
    }

    // Dummy method that returns @PolyDet("down").
    @PolyDet("down") String retPolyDown(@PolyDet PolyUpTest this) {
        return null;
    }

    // Static version.
    static @PolyDet("down") String retPolyDownStatic(@PolyDet PolyUpTest p) {
        return null;
    }

    // Tests that @PolyDet("up") is converted to @PolyDet.
    @PolyDet String testPolyUpNonCollection(@PolyDet PolyUpTest p) {
        return p.retPolyUp();
    }

    @PolyDet String testPolyUpNonCollectionStatic(@PolyDet PolyUpTest p) {
        return retPolyUpStatic(p);
    }

    // Tests that @PolyDet("down") is not converted to @PolyDet.
    @PolyDet("down") String testPolyDownNonCollection(@PolyDet PolyUpTest p) {
        return p.retPolyDown();
    }

    @PolyDet("down") String testPolyDownNonCollectionStatic(@PolyDet PolyUpTest p) {
        return retPolyDownStatic(p);
    }

    // Tests @PolyDet("up") conversion doesn't happen if @PolyDet("up") is passed rather than
    // @PolyDet.
    @PolyDet String testPolyUpParameter(@PolyDet("up") PolyUpTest p) {
        // :: error: (return.type.incompatible)
        return p.retPolyUp();
    }
}
