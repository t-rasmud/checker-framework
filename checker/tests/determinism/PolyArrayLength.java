import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
public class PolyArrayLength {
    @PolyDet("down") int size;

    @PolyDet int polySize;

    @PolyDet("use") int @PolyDet("use") [] myArr;

    @PolyDet("down") int arrLength(@PolyDet int @PolyDet [] arr) {
        return arr.length;
    }

    void assignArrLength(@PolyDet int @PolyDet [] arr) {
        polySize = arr.length;
        size = myArr.length;
    }
}
