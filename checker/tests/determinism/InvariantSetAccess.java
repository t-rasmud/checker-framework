import java.util.ArrayList;
import java.util.HashSet;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class InvariantSetAccess {
    void test(@OrderNonDet HashSet<@Det ArrayList<@Det String>> st) {
        @NonDet ArrayList<@Det String> lst = st.iterator().next();
        // :: error: (assingment.type.incompatible)
        @Det String s = lst.get(0);
    }

    void test1(
            @OrderNonDet ArrayList<@Det ArrayList<@Det String>> lst,
            @Det ArrayList<@Det String> lst1,
            @NonDet int index) {
        lst.add(lst1);
        @NonDet ArrayList<@Det String> tmp = lst.get(0);
        tmp.add(index, "");
        lst1.add(index, "");
    }
}
