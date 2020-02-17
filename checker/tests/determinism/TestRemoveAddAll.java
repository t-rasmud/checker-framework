import java.util.ArrayList;
import java.util.LinkedList;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class TestRemoveAddAll {
    void testRemove(@OrderNonDet ArrayList<@Det String> lst, @Det int index) {
        // :: error: (method.invocation.invalid)
        lst.remove(index);
    }

    void testAddall(
            @OrderNonDet LinkedList<@Det String> lst,
            @OrderNonDet LinkedList<@Det String> lst1,
            @Det LinkedList<@Det String> lst2) {
        lst.addAll(lst1);
        lst.addAll(lst2);
    }
}
