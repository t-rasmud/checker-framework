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

    void method(@NonDet int i, @Det String @Det [] array) {
        // :: error: (assignment.type.incompatible)
        @Det String s = array[i];
        // :: error: (invalid.array.assignment)
        array[i] = "";
    }

    @PolyDet boolean method2(@PolyDet int i, @Det String @Det [] array, @NonDet String @NonDet [] array1) {
        // :: error: (assignment.type.incompatible)
        @Det String s = array[i];
        // :: error: (assignment.type.incompatible)
        @Det String s1 = array1[i];
        // :: error: (invalid.array.assignment)
        array[i] = "";
        return true;
    }

    @PolyDet boolean method3(@PolyDet int i, @NonDet String @NonDet [] array) {
        // :: error: (assignment.type.incompatible)
        @PolyDet String s = array[i];
        array[i] = "";
        return true;
    }

    // :: error: (invalid.array.component.type)
    void polyIndex(@PolyDet int @NonDet [] a) {}

    // :: error: (invalid.array.component.type)
    void polyIndex1(@PolyDet int @Det [] a) {}

    // :: error: (invalid.array.component.type)
    void polyIndex2(@PolyDet int @OrderNonDet [] a) {}
}
