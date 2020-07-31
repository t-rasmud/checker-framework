import java.util.List;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class TypeParamDefaultsDet {
    // :: error: (invalid.element.type)
    void test(List<@PolyDet String> lst) {
        // :: error: (invalid.element.type) :: error: (assignment.type.incompatible)
        @PolyDet List<@PolyDet String> lst1 = lst;
    }

    // :: error: (invalid.element.type)
    void test1(List<@PolyDet List<@PolyDet String>> lst) {
        // :: error: (invalid.element.type)  :: error: (assignment.type.incompatible)
        @PolyDet List<@PolyDet List<@PolyDet String>> lst1 = lst;
    }
}
