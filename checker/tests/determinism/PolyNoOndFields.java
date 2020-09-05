import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
@CollectionType
public class PolyNoOndFields {
    @PolyDet("noOrderNonDet") ArrayList<@PolyDet("useNoOrderNonDet") String> fld;

    @PolyDet ArrayList<@PolyDet("down") String> fld1;
}

class PolyTestClass {
    static void test(@OrderNonDet ArrayList<@Det String> lst) {
        @OrderNonDet PolyNoOndFields inst = new @OrderNonDet PolyNoOndFields();
        // :: error: (invalid.field.assignment)
        inst.fld = lst;
        inst.fld1 = lst;
    }
}
