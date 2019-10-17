import org.checkerframework.checker.determinism.qual.PolyDet;

public class PolyArraysIssue {
    void myPolyInt(@PolyDet Integer arg) {}

    public void equals(@PolyDet Integer @PolyDet [] a, @PolyDet int index) {
        @PolyDet("up") Integer arrayVal = a[index];
        myPolyInt(arrayVal);
        myPolyInt(a[index]);
    }
}
