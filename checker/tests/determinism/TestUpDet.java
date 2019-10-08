package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestUpDet {
    static @PolyDet("upDet") List<@Det String> retDetUp(@PolyDet List<@Det String> list) {
        return new ArrayList<>();
    }

    public @Det List<@Det String> testReturnTypeResolve1(@Det List<@Det String> list) {
        // :: error: (return.type.incompatible)
        return retDetUp(list);
    }

    public @OrderNonDet List<@Det String> testReturnTypeResolve2(@Det List<@Det String> list) {
        return retDetUp(list);
    }

    public @Det List<@Det String> testReturnTypeResolve3(@OrderNonDet List<@Det String> list) {
        // :: error: (return.type.incompatible)
        return retDetUp(list);
    }

    public @OrderNonDet List<@Det String> testReturnTypeResolve4(
            @OrderNonDet List<@Det String> list) {
        return retDetUp(list);
    }

    public @PolyDet("upDet") List<@Det String> testReturnTypeResolve5(
            @PolyDet List<@Det String> list) {
        return retDetUp(list);
    }

    public void testSubtypes(@PolyDet List<@Det String> l1, @PolyDet List<@Det String> l2) {
        @PolyDet("upDet") List<@Det String> a = l1;
        @PolyDet("upDet") List<@Det String> b = l2;
    }

    public void testSubtypesInvalid(@PolyDet List<@Det String> l1, @PolyDet List<@Det String> l2) {
        @PolyDet("up") List<@Det String> a = l1;
        @PolyDet List<@Det String> b = l1;
        // :: error: (assignment.type.incompatible)
        @PolyDet("down") List<@Det String> c = l1;
        @PolyDet("upDet") List<@Det String> d = l2;
    }
}
