import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class InvariantSubtyping {
    void testNdListAddOndList(
            @NonDet List<@NonDet List<@NonDet String>> lst, @OrderNonDet List<@Det String> ad) {
        // :: error: (argument.type.incompatible)
        lst.add(ad);
    }

    void testNdListAddDetStr(@NonDet List<@NonDet String> lst, @Det String ad) {
        lst.add(ad);
    }

    // :: error: (ordernondet.on.noncollection.and.nonarray)
    void testNdListAddOndObject(@NonDet List<@NonDet Object> lst, @OrderNonDet Object ad) {
        // :: error: (ordernondet.on.noncollection.and.nonarray)
        lst.add(ad);
    }
}
