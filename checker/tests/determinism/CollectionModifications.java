import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class CollectionModifications {
    // Add an element without specifying index
    void listNonDetAddND(@NonDet List<@NonDet String> lst, @NonDet String str) {
        lst.add(str);
    }

    void listNonDetAddOND(
            @NonDet List<@NonDet List<@NonDet String>> lst, @OrderNonDet List<@Det String> str) {
        // :: error: (argument.type.incompatible)
        lst.add(str);
        // :: error: (invariant.cast.unsafe)
        lst.add((@NonDet List<@NonDet String>) str);
    }

    void listNonDetAddD(@NonDet List<@NonDet String> lst, @Det String str) {
        lst.add(str);
    }

    void listNonDetAddDList(
            @NonDet List<@NonDet List<@NonDet String>> lst, @Det List<@Det String> str) {
        // :: error: (argument.type.incompatible)
        lst.add(str);
    }

    void listOrderNonDetAddND(@OrderNonDet List<@Det String> lst, @NonDet String str) {
        // :: error: (argument.type.incompatible)
        lst.add(str);
    }

    void listOrderNonDetAddOND(
            @OrderNonDet List<@OrderNonDet List<@Det String>> lst,
            @OrderNonDet List<@Det String> str) {
        lst.add(str);
    }

    void listOrderNonDetAddD(@OrderNonDet List<@Det String> lst, @Det String str) {
        lst.add(str);
    }

    void listDetAddND(@Det List<@Det String> lst, @NonDet String str) {
        // :: error: (argument.type.incompatible)
        lst.add(str);
    }

    void listDetAddOND(@Det List<@Det List<@Det String>> lst, @OrderNonDet List<@Det String> str) {
        // :: error: (argument.type.incompatible)
        lst.add(str);
    }

    void listDetAddD(@Det List<@Det String> lst, @Det String str) {
        lst.add(str);
    }

    // Add an elements at a specific index
    void listNonDetSetND(
            @NonDet List<@NonDet String> lst,
            @NonDet String str,
            @NonDet int nIndex,
            @Det int dIndex) {
        lst.set(nIndex, str);
        lst.set(dIndex, str);
    }

    void listNonDetSetD(
            @NonDet List<@NonDet String> lst,
            @Det String str,
            @NonDet int nIndex,
            @Det int dIndex) {
        lst.set(nIndex, str);
        lst.set(dIndex, str);
    }

    void listOrderNonDetSetND(
            @OrderNonDet List<@Det String> lst,
            @NonDet String str,
            @NonDet int nIndex,
            @Det int dIndex) {
        // :: error: (argument.type.incompatible) :: error: (method.invocation.invalid)
        lst.set(nIndex, str);
        // :: error: (argument.type.incompatible) :: error: (method.invocation.invalid)
        lst.set(dIndex, str);
    }

    void listOrderNonDetSetOND(
            @OrderNonDet List<@OrderNonDet List<@Det String>> lst,
            @OrderNonDet List<@Det String> str,
            @NonDet int nIndex,
            @Det int dIndex) {
        // :: error: argument.type.incompatible :: error: (method.invocation.invalid)
        lst.set(nIndex, str);
        // :: error: method.invocation.invalid
        lst.set(dIndex, str);
    }

    void listOrderNonDetSetD(
            @OrderNonDet List<@Det String> lst,
            @Det String str,
            @NonDet int nIndex,
            @Det int dIndex) {
        // :: error: argument.type.incompatible :: error: (method.invocation.invalid)
        lst.set(nIndex, str);
        // :: error: method.invocation.invalid
        lst.set(dIndex, str);
    }

    void listDetSetND(
            @Det List<@Det String> lst, @NonDet String str, @NonDet int nIndex, @Det int dIndex) {
        // :: error: (argument.type.incompatible)
        lst.set(nIndex, str);
        // :: error: (argument.type.incompatible)
        lst.set(dIndex, str);
    }

    void listDetSetD(
            @Det List<@Det String> lst, @Det String str, @NonDet int nIndex, @Det int dIndex) {
        // :: error: (argument.type.incompatible)
        lst.set(nIndex, str);
        lst.set(dIndex, str);
    }

    // Remove an element without specifying index
    void setNonDetRemoveND(@NonDet Set<@NonDet String> set, @NonDet String str) {
        set.remove(str);
    }

    void setNonDetRemoveOND(
            @NonDet Set<@NonDet Set<@NonDet String>> set, @OrderNonDet Set<@Det String> str) {
        set.remove(str);
    }

    void setNonDetRemoveD(@NonDet Set<@NonDet String> set, @Det String str) {
        set.remove(str);
    }

    void setOrderNonDetRemoveND(@OrderNonDet Set<@Det String> set, @NonDet String str) {
        // :: error: (argument.type.incompatible)
        set.remove(str);
    }

    void setOrderNonDetRemoveOND(
            @OrderNonDet Set<@OrderNonDet Set<@Det String>> set,
            @OrderNonDet Set<@Det String> str) {
        set.remove(str);
    }

    void setOrderNonDetRemoveD(@OrderNonDet Set<@Det String> set, @Det String str) {
        set.remove(str);
    }

    void setDetRemoveND(@Det Set<@Det String> set, @NonDet String str) {
        // :: error: (argument.type.incompatible)
        set.remove(str);
    }

    void setDetRemoveD(@Det Set<@Det String> set, @Det String str) {
        set.remove(str);
    }

    // Remove an element at a specific index
    void listNonDetRemove(@NonDet List<@NonDet String> lst, @NonDet int nIndex, @Det int dIndex) {
        lst.remove(nIndex);
        lst.remove(dIndex);
    }

    void listOrderNonDetRemove(
            @OrderNonDet List<@Det String> lst, @NonDet int nIndex, @Det int dIndex) {
        // :: error: (argument.type.incompatible) :: error: (method.invocation.invalid)
        lst.remove(nIndex);
        // :: error: (method.invocation.invalid)
        lst.remove(dIndex);
    }

    void listDetRemove(@Det List<@Det String> lst, @NonDet int nIndex, @Det int dIndex) {
        // :: error: (argument.type.incompatible)
        lst.remove(nIndex);
        lst.remove(dIndex);
    }
}
