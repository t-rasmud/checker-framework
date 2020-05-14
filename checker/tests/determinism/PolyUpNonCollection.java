import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
public class PolyUpNonCollection {
    void nonCollection(@PolyDet int x) {}

    void nonCollection1(@PolyDet int x, @PolyDet PolyUpNonCollection arg) {}

    void collectionMethod(@PolyDet List<@PolyDet("down") Integer> lst) {
        int x = lst.get(0);
        nonCollection(x);
    }

    void collectionMethod1(@PolyDet List<@PolyDet Integer> lst, @PolyDet PolyUpNonCollection arg) {
        int x = lst.get(0);
        nonCollection1(x, arg);
    }

    void callCollectionMethod(
            @PolyDet List<@PolyDet("down") Integer> lst, @OrderNonDet List<@Det Integer> lst1) {
        collectionMethod(lst);
        // :: error: (argument.type.incompatible) :: error: (method.invocation.invalid)
        collectionMethod(lst1);
    }

    static void collectionMethod2(@PolyDet ClassWithListField c) {
        for (@PolyDet("up") String elt : c.list) {
            @PolyDet String s = elt;
        }
    }

    @HasQualifierParameter(NonDet.class)
    public static class ClassWithListField {
        @PolyDet List<@PolyDet String> list;
    }
}
