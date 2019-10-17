import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class PolyArraysIssue {
    void myPolyInt(@PolyDet Integer arg) {}

    void myPolyMethod(@PolyDet int arg) {}

    public void equals(
            @PolyDet Integer @PolyDet [] a,
            @PolyDet ArrayList<@PolyDet Integer> l,
            @PolyDet int index) {
        @PolyDet("up") Integer arrayVal = a[index];
        myPolyInt(arrayVal);
        myPolyInt(a[index]);
        @PolyDet("up") Integer listVal = l.get(index);
        myPolyMethod(listVal);
    }
}
