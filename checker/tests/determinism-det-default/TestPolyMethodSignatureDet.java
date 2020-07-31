import java.util.List;
import org.checkerframework.checker.determinism.qual.*;

public class TestPolyMethodSignatureDet {
    // :: error: (invalid.polydet.up)
    static void testPolyUpInvalid(@PolyDet("up") Integer a) {}

    void testPolyUpValid(@PolyDet TestPolyMethodSignatureDet this, @PolyDet("up") Integer a) {}

    // :: error: (invalid.polydet.up)
    void testPolyUpValid1(@PolyDet("up") Integer a) {}

    static @PolyDet("up") int checkListValid(@PolyDet List<@PolyDet Integer> lst) {
        return 0;
    }

    // :: error: (invalid.polydet.up)
    static @PolyDet("up") int checkListInvalid(@PolyDet("up") List<@PolyDet Integer> lst) {
        return 0;
    }

    static <T extends @PolyDet Object> @PolyDet("down") int checkListInvalid2(
            // :: error: (invalid.polydet.up)
            @PolyDet("up") List<T> lst) {
        return 0;
    }
}
