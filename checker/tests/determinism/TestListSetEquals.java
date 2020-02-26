import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestListSetEquals {
    void testListEquals1(@Det List<@Det Integer> aList, @Det List<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals2(@Det List<@Det Integer> aList, @OrderNonDet List<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals3(@Det List<@Det Integer> aList, @NonDet List<@NonDet Integer> bList) {
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals4(@OrderNonDet List<@Det Integer> aList, @Det List<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals5(
            @OrderNonDet List<@Det Integer> aList, @OrderNonDet List<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals6(@NonDet List<@NonDet Integer> aList, @Det List<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals7(
            @NonDet List<@NonDet Integer> aList, @OrderNonDet List<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    void testListEquals8(@NonDet List<@NonDet Integer> aList, @NonDet List<@NonDet Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    // List equals Set
    void testListSetEquals1(@Det List<@Det Integer> aList, @Det Set<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testListSetEquals2(@Det List<@Det Integer> aList, @OrderNonDet Set<@Det Integer> set) {
        @Det boolean ret = aList.equals(set);
    }

    void testListSetEquals3(@OrderNonDet List<@Det Integer> aList, @Det Set<@Det Integer> set) {
        @Det boolean ret = aList.equals(set);
    }

    void testListSetEquals4(
            @OrderNonDet List<@Det Integer> list, @OrderNonDet Set<@Det Integer> set) {
        @Det boolean ret = list.equals(set);
    }

    void testListSetEquals5(@NonDet List<@NonDet Integer> list, @Det Set<@Det Integer> set) {
        @Det boolean ret = list.equals(set);
    }

    void testListSetEquals6(
            @NonDet List<@NonDet Integer> list, @OrderNonDet Set<@Det Integer> set) {
        @Det boolean ret = list.equals(set);
    }

    void testListSetEquals7(@NonDet List<@NonDet Integer> list, @NonDet Set<@NonDet Integer> set) {
        @Det boolean ret = list.equals(set);
    }

    // Set equals set
    void testSetEquals1(@Det Set<@Det Integer> aList, @Det Set<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testSetEquals2(@Det Set<@Det Integer> aList, @OrderNonDet Set<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testSetEquals3(@OrderNonDet Set<@Det Integer> aList, @Det Set<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testSetEquals4(
            @OrderNonDet Set<@Det Integer> aList, @OrderNonDet Set<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testSetEquals5(@NonDet Set<@NonDet Integer> aList, @Det Set<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    void testSetEquals6(@NonDet Set<@NonDet Integer> aList, @OrderNonDet Set<@Det Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    void testSetEquals7(@NonDet Set<@NonDet Integer> aList, @NonDet Set<@NonDet Integer> bList) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = aList.equals(bList);
    }

    // Set equals List
    void testSetListEquals1(@Det Set<@Det Integer> aList, @Det List<@Det Integer> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testSetListEqual2(@OrderNonDet Set<@Det Integer> set, @Det List<@Det Integer> list) {
        @Det boolean ret = set.equals(list);
    }

    void testSetListEqual3(@Det Set<@Det Integer> set, @OrderNonDet List<@Det Integer> list) {
        @Det boolean ret = set.equals(list);
    }

    void testSetListEqual4(
            @OrderNonDet Set<@Det Integer> set, @OrderNonDet List<@Det Integer> list) {
        @Det boolean ret = set.equals(list);
    }

    void testSetListEqual5(@Det Set<@Det Integer> set, @NonDet List<@NonDet Integer> list) {
        @Det boolean ret = set.equals(list);
    }

    void testSetListEqual6(@OrderNonDet Set<@Det Integer> set, @NonDet List<@NonDet Integer> list) {
        @Det boolean ret = set.equals(list);
    }

    void testSetListEqual7(@NonDet Set<@NonDet Integer> set, @NonDet List<@NonDet Integer> list) {
        @Det boolean ret = set.equals(list);
    }

    void testSetSetEqualsNested(
            @OrderNonDet Set<@OrderNonDet Set<@Det Integer>> aList,
            @OrderNonDet Set<@OrderNonDet Set<@Det Integer>> bList) {
        @Det boolean ret = aList.equals(bList);
    }

    void testSetListEqualsNested(
            @OrderNonDet Set<@OrderNonDet Set<@Det Integer>> setSet,
            @OrderNonDet Set<@OrderNonDet List<@Det Integer>> setList) {
        @Det boolean ret = setSet.equals(setList);
    }

    void testDifferentTypes(
            @NonDet List<@NonDet List<@NonDet Integer>> lst,
            @NonDet List<@NonDet List<@NonDet String>> lst1) {
        @Det boolean ret = lst.equals(lst1);
    }

    void testDifferentTypes1(
            @NonDet List<@NonDet List<@NonDet Integer>> lst,
            @NonDet List<@NonDet List<@NonDet List<@NonDet String>>> lst1) {
        @Det boolean ret = lst.equals(lst1);
    }

    void testDifferentTypes2(
            @NonDet List<@NonDet List<@NonDet Integer>> lst,
            @NonDet List<@NonDet List<@NonDet List<@NonDet String>>> lst1) {
        @Det boolean ret = lst1.equals(lst);
    }

    void testDifferentTypes3(
            @NonDet List<@NonDet ArrayList<@NonDet Integer>> lst,
            @NonDet List<@NonDet LinkedList<@NonDet Integer>> lst1) {
        // :: error: (assignment.type.incompatible)
        @Det boolean ret = lst1.equals(lst);
    }
}
