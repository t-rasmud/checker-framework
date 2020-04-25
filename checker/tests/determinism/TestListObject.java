import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestListObject {
    void testContains1(@Det List<@Det Integer> lst, @Det Integer obj) {
        @Det boolean res = lst.contains(obj);
    }

    void testContains2(@OrderNonDet List<@Det Integer> lst, @Det Integer obj) {
        @Det boolean res = lst.contains(obj);
    }

    void testContains3(@Det List<@Det List<@Det Integer>> lst, @Det List<@Det Integer> obj) {
        @Det boolean res = lst.contains(obj);
    }

    void testContains4(
            @OrderNonDet List<@Det List<@Det Integer>> lst, @Det List<@Det Integer> obj) {
        @Det boolean res = lst.contains(obj);
    }

    void testContains5(
            @OrderNonDet List<@OrderNonDet List<@Det Integer>> lst,
            @OrderNonDet List<@Det Integer> obj) {
        @Det boolean res = lst.contains(obj);
    }
}
