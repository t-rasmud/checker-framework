import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.determinism.qual.*;

public class TestListUnsafe1 {
    void TestList() {
        @OrderNonDet List<@Det Integer> lst = new @OrderNonDet ArrayList<@Det Integer>();
        @NonDet int rt = lst.remove(10);
    }
}
