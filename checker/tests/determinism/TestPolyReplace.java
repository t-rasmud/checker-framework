import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestPolyReplace {
    // :: error: (invalid.upper.bound.on.type.argument)
    <T> void testPolyDown(@OrderNonDet Set<T> set, @Det T elem) {
        @Det boolean out = set.contains(elem);
    }

    @Det int @PolyDet("up") [][] testArrayPolyUp(@Det int @PolyDet [][] arr) {
        return arr;
    }

    @PolyDet("up") int @PolyDet("up") [] @PolyDet("up") [] testArrayPolyUp1(int[][] arr) {
        return arr;
    }

    @PolyDet("up") List<List<Integer>> polyList(@PolyDet List<List<Integer>> list) {
        // :: error: (return.type.incompatible)
        return list;
    }
}
