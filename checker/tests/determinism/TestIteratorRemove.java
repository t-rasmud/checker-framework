package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestIteratorRemove {
    static void testRemoveDet(@Det List<@Det String> list) {
        // :: error: (assignment.type.incompatible)
        @OrderNonDet Iterator<@Det String> iter = list.iterator();
        iter.next();
        // :: error: (method.invocation.invalid)
        iter.remove();
    }

    static void testRemoveNonDet(@NonDet List<@NonDet String> list) {
        @NonDet Iterator<@NonDet String> iter = list.iterator();
        iter.next();
        iter.remove();
    }

    static void testRemovePolyNoOND(
            @PolyDet("noOrderNonDet") List<@PolyDet("useNoOrderNonDet") String> list) {
        @PolyDet("noOrderNonDet") Iterator<@PolyDet("useNoOrderNonDet") String> iter = list.iterator();
        iter.next();
        iter.remove();
    }

    static void testRemoveOrderNonDet(@OrderNonDet List<@Det String> list) {
        @OrderNonDet Iterator<@Det String> iter = list.iterator();
        iter.next();
        // :: error: (method.invocation.invalid)
        iter.remove();
    }

    static void testRemovePoly(@PolyDet List<@PolyDet String> list) {
        @PolyDet Iterator<@PolyDet String> iter = list.iterator();
        iter.next();
        // :: error: (method.invocation.invalid)
        iter.remove();
    }
}
