import org.checkerframework.checker.determinism.qual.PolyDet;

public class PolyArrayAccess {
    void test(
            @PolyDet("use") Object @PolyDet [] arr1,
            @PolyDet("up") Object @PolyDet("up") [] arr2,
            @PolyDet("use") int index) {
        arr2[index] = arr1[index];
    }
}
