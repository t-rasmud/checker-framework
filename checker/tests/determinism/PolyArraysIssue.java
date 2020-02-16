import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class PolyArraysIssue {
    void myPolyInt(@PolyDet Integer arg) {}

    public void equals(@PolyDet Integer @PolyDet [] a, @PolyDet int index) {
        @PolyDet("up") Integer arrayVal = a[index];
        myPolyInt(arrayVal);
        myPolyInt(a[index]);
        // :: error: (invalid.array.assignment)
        a[0] = 5;
    }

    void method(@Det String @Det [] array, @NonDet int i, @Det int di, @PolyDet int pi) {
        // :: error: (assignment.type.incompatible)
        @Det String s = array[i];
        // :: error: (invalid.array.assignment)
        array[i] = "";

        @Det String t = array[di];
        array[di] = "";

        // :: error: (assignment.type.incompatible)
        @Det String p = array[pi];
        // :: error: (invalid.array.assignment)
        array[pi] = "";
    }

    @PolyDet boolean method2(
            @Det String @Det [] array,
            @NonDet String @NonDet [] array1,
            @PolyDet int i,
            @Det int di,
            @NonDet int ni) {
        // :: error: (assignment.type.incompatible)
        @Det String s = array[i];
        // :: error: (assignment.type.incompatible)
        @Det String s1 = array1[i];
        // :: error: (invalid.array.assignment)
        array[i] = "";

        // :: error: (assignment.type.incompatible)
        @Det String t1 = array1[di];
        array1[di] = "";

        // :: error: (assignment.type.incompatible)
        @Det String p1 = array1[ni];
        array1[ni] = "";
        return true;
    }

    @PolyDet boolean method3(@PolyDet int i, @NonDet String @NonDet [] array) {
        // :: error: (assignment.type.incompatible)
        @PolyDet String s = array[i];
        array[i] = "";
        return true;
    }

    void method4(@PolyDet String @PolyDet [] array, @NonDet int i, @Det int di, @PolyDet int pi) {
        // :: error: (assignment.type.incompatible)
        @Det String s = array[i];
        // :: error: (invalid.array.assignment)
        array[i] = "";

        // :: error: (assignment.type.incompatible)
        @Det String t = array[di];
        // :: error: (invalid.array.assignment)
        array[di] = "";

        // :: error: (assignment.type.incompatible)
        @Det String p = array[pi];
        // :: error: (invalid.array.assignment)
        array[pi] = "";
    }

    void polyIndex(@PolyDet int @NonDet [] a) {}

    // :: error: (invalid.array.component.type)
    void polyIndex1(@PolyDet int @Det [] a) {}

    // :: error: (invalid.array.component.type)
    void polyIndex2(@PolyDet int @OrderNonDet [] a) {}
}
