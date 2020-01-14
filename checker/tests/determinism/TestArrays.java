package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestArrays {
    // Tests whether array parameters have correct defaults when passed.
    void testArrParam(@PolyDet int @PolyDet [] arr) {
        @PolyDet int @PolyDet [] a = new @PolyDet int @PolyDet [] {10};
        takeArr(arr);
    }

    // Tests whether array return values have correct defaults.
    void testArrRet() {
        // :: error: (assignment.type.incompatible)
        @Det int i = returnArr()[0];
        @Det int j = returnArrExplicit()[0];
    }
    // Tests whether returned arrays have correct defaults within methods.
    int[] testArrRetInternal() {
        @PolyDet int @PolyDet [] arr = new @PolyDet int @PolyDet [0];
        // This should fail if @Det int [] @PolyDet is expected, which it
        // should't be.
        return new @PolyDet int @PolyDet [0];
    }

    int[] returnArr() {
        return new @PolyDet int @PolyDet [] {0};
    }

    @Det int @Det [] returnArrExplicit() {
        return new @Det int @Det [] {0};
    }

    void takeArr(int[] a) {}

    void testArr(int[] a) {
        // :: error: (assignment.type.incompatible)
        @Det int i = a[0];
    }

    void testArr1(@Det int @Det [] a) {
        @Det int i = a[0];
        @Det String str = "rash";
    }

    void testArrayLength(@Det int @Det [] a) {
        System.out.println(a.length);
    }

    void testArrayLength2(@Det int @OrderNonDet [] a) {
        System.out.println(a.length);
    }

    static int[] foo(int @Det [] arr) {
        @NonDet int @NonDet [] a = new @NonDet int @NonDet [20];
        // :: error: (return.type.incompatible)
        return a;
    }

    void checkArrayAccessLvalue(@Det int @OrderNonDet [] x, @NonDet int i, @Det int y) {
        // :: error: (invalid.array.assignment)
        x[i] = y;
    }

    // :: error: (invalid.array.component.type)
    void checkArrayValid2D(@Det int @OrderNonDet [] @NonDet [] arr) {}

    int[] @Det [] checkDefaults2D(int[][] arr) {
        // :: error: (return.type.incompatible)
        return arr;
    }
}