import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class InvariantSetEquals {
    void testOtherType(@NonDet Set<@Det String> st, @NonDet List<@Det String> lst) {
        @Det boolean result = st.equals(lst);
    }

    void testOtherType1(@NonDet Set<@Det String> st, @Det List<@Det String> lst) {
        @Det boolean result = st.equals(lst);
    }

    void testOtherSetType(@NonDet Set<@Det String> st, @Det Set<@Det Integer> st1) {
        @Det boolean result = st.equals(st1);
    }

    void testOtherSetType1(
            @NonDet Set<@Det Set<@Det String>> st, @NonDet Set<@Det List<@Det String>> st1) {
        @Det boolean result = st.equals(st1);
    }

    void testOtherObject(@NonDet Set<@Det String> st, @Det Object obj) {
        @Det boolean result = st.equals(obj);
    }

    void testNDSet(@NonDet Set<@Det String> st, @NonDet Set<@Det String> st1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = st.equals(st1);
    }

    void testNDSet1(@NonDet Set<@Det String> st, @OrderNonDet Set<@Det String> st1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = st.equals(st1);
    }

    void testNDSet2(@NonDet Set<@Det String> st, @Det Set<@Det String> st1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean result = st.equals(st1);
    }

    void testONDSet(@OrderNonDet Set<@Det String> st, @NonDet Set<@Det String> st1) {
        // :: error: (assignment.type.incompatible)  :: error: (argument.type.incompatible)
        @Det boolean result = st.equals(st1);
    }

    void testONDSet1(@OrderNonDet Set<@Det String> st, @OrderNonDet Set<@Det String> st1) {
        @Det boolean result = st.equals(st1);
    }

    void testONDSet2(@OrderNonDet Set<@Det String> st, @Det Set<@Det String> st1) {
        @Det boolean result = st.equals(st1);
    }

    void testDetSet(@Det Set<@Det String> st, @NonDet Set<@Det String> st1) {
        // :: error: (argument.type.incompatible)
        @Det boolean result = st.equals(st1);
    }

    void testDetSet1(@Det Set<@Det String> st, @OrderNonDet Set<@Det String> st1) {
        @Det boolean result = st.equals(st1);
    }

    void testDetSet2(@Det Set<@Det String> st, @Det Set<@Det String> st1) {
        @Det boolean result = st.equals(st1);
    }
}
