import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestArraysSort {
    void testSort(@Det int @Det [] a) {
        Arrays.sort(a);
    }

    void testSort1(@Det int @OrderNonDet [] a) {
        // :: error: (argument.type.incompatible)
        System.out.println(a[0]);
        Arrays.sort(a);
        System.out.println(a[0]);
    }

    void testSort2(@Det Integer @OrderNonDet [] a) {
        // :: error: (argument.type.incompatible)
        System.out.println(a[0]);
        @Det IntComparator c = new @Det IntComparator();
        Arrays.sort(a);
        Arrays.sort(a, c);
        System.out.println(a[0]);
    }

    void testSort3(@Det Integer @OrderNonDet [] a, @Det Comparator<Integer> c) {
        Arrays.sort(a, c);
        System.out.println(a[0]);
    }

    void testSort4(@Det Integer @OrderNonDet [] a, @NonDet Comparator<Integer> c) {
        // :: error: (argument.type.incompatible)
        Arrays.sort(a, c);
        // :: error: (argument.type.incompatible)
        System.out.println(a[0]);
    }

    void testSort5(
            @OrderNonDet List<@Det Integer> @OrderNonDet [] a,
            @Det Comparator<@OrderNonDet List<@Det Integer>> c) {
        // :: error: (type.argument.type.incompatible)
        Arrays.sort(a, c);
        // :: error: argument.type.incompatible :: error: invalid.element.type
        System.out.println(a[0]);
    }

    void testSort6(@Det int @PolyDet [] a) {
        Arrays.sort(a);
        @Det int @PolyDet("down") [] tmp = a;
    }

    void testSort7(@PolyDet int @PolyDet [] a) {
        // :: error: (argument.type.incompatible)
        Arrays.sort(a);
        // :: error: assignment.type.incompatible
        @PolyDet("down") int @PolyDet("down") [] tmp = a;
    }

    <T> void testSort8(@Det T @OrderNonDet [] a) {
        Arrays.sort(a);
        // :: error: (nondeterministic.tostring)
        System.out.println(a);
    }

    <T extends @Det Object> void testSort9(T @OrderNonDet [] a) {
        Arrays.sort(a);
        // :: error: (nondeterministic.tostring)
        System.out.println(a);
    }
}

class IntComparator implements Comparator<@NonDet Integer> {
    public @NonDet int compare(@NonDet Integer i1, @NonDet Integer i2) {
        return 0;
    }
}
