import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class InvariantListEquals {
    void testOtherType(@NonDet List<@Det String> lst, @NonDet Set<@Det String> st) {
        @Det boolean result = lst.equals(st);
    }

    void testOtherType1(@NonDet List<@Det String> lst, @Det Set<@Det String> st) {
        @Det boolean result = lst.equals(st);
    }

    void testOtherListType(@NonDet List<@Det String> lst, @Det List<@Det Integer> lst1) {
        @Det boolean result = lst.equals(lst1);
    }

    void testOtherListType1(
            @NonDet List<@Det Set<@Det String>> lst, @NonDet List<@Det List<@Det String>> lst1) {
        @Det boolean result = lst.equals(lst1);
    }

    void testOtherObject(@NonDet List<@Det String> lst, @Det Object obj) {
        @Det boolean result = lst.equals(obj);
    }

    void testNDList(@NonDet List<@Det String> lst, @NonDet List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = lst.equals(lst1);
    }

    void testNDList1(@NonDet List<@Det String> lst, @OrderNonDet List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = lst.equals(lst1);
    }

    void testNDList2(@NonDet List<@Det String> lst, @Det List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = lst.equals(lst1);
    }

    void testONDList(@OrderNonDet List<@Det String> lst, @NonDet List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible)  :: error: (method.invocation.invalid)
        @Det boolean result = lst.equals(lst1);
    }

    void testONDList1(@OrderNonDet List<@Det String> lst, @OrderNonDet List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = lst.equals(lst1);
    }

    void testONDList2(@OrderNonDet List<@Det String> lst, @Det List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = lst.equals(lst1);
    }

    void testDetList(@Det List<@Det String> lst, @NonDet List<@Det String> lst1) {
        // :: error: (argument.type.incompatible) :: error: (method.invocation.invalid)
        @Det boolean result = lst.equals(lst1);
    }

    void testDetList1(@Det List<@Det String> lst, @OrderNonDet List<@Det String> lst1) {
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        @Det boolean result = lst.equals(lst1);
    }

    void testDetList2(@Det List<@Det String> lst, @Det List<@Det String> lst1) {
        @Det boolean result = lst.equals(lst1);
    }
}
