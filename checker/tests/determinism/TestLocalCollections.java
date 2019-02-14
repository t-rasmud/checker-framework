package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestLocalCollections {
    void testList1() {
        // Verfies this defaults to @NonDet List<@NonDet String> rather than @NonDet List<@Det
        // String>, which is invalid.
        List<String> a;
    }

    void testList2(@NonDet List<@NonDet String> a) {
        List<String> b = a;
    }

    @NonDet List<@NonDet String> testList3(@NonDet List<@NonDet String> a) {
        List<String> b = a;
        return b;
    }

    void testList4() {
        // :: error: (invalid.element.type)
        @NonDet List<String> a;
    }

    void testList5() {
        @Det List<String> list;
    }

    void testNestedList1() {
        List<List<String>> a;
    }

    void testNestedList2() {
        @Det List<List<String>> a;
    }

    void testNestedList3(@NonDet List<@NonDet List<@NonDet String>> a) {
        List<List<String>> b = a;
    }

    void testArray1() {
        String[] a;
    }

    void testArray2(@NonDet String @NonDet [] a) {
        String[] b = a;
    }

    @NonDet String @NonDet [] testArray3(@NonDet String @NonDet [] a) {
        @NonDet String @NonDet [] b = a;
        return b;
    }

    void testArray4() {
        // :: error: (invalid.array.component.type)
        String @NonDet [] a;
    }

    void testArray5() {
        String @Det [] list;
    }

    void testNestedArray1() {
        String[][] a;
    }

    void testNestedArray2(@NonDet String @NonDet [] @NonDet [] a) {
        String[][] b = a;
    }
}
