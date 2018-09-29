import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestArrayListWildcard {
    void testAddAll(
            @OrderNonDet ArrayList<@Det Integer> arList, @Det ArrayList<@Det Integer> elems) {
        @Det boolean ret = arList.addAll(elems);
    }

    void testAddAll1(
            @OrderNonDet ArrayList<@Det Object> arList, @Det ArrayList<@Det Integer> elems) {
        @Det boolean ret = arList.addAll(elems);
    }

    void testAddAll2(
            @NonDet ArrayList<@Det Object> arList, @NonDet ArrayList<@NonDet Integer> elems) {
        // :: error: (argument.type.incompatible)
        arList.addAll(elems);
    }

    void testAddAll3(
            @NonDet ArrayList<@NonDet Object> arList, @NonDet ArrayList<@NonDet Integer> elems) {
        arList.addAll(elems);
    }

    void testConstructor(@OrderNonDet ArrayList<@Det String> arList) {
        @OrderNonDet Collection<@Det String> coll = arList;
        // :: error: (assignment.type.incompatible)
        @Det ArrayList<@Det String> newList = new ArrayList<String>(coll);
    }

    void testConstructor1(@NonDet ArrayList<@Det String> arList) {
        @NonDet Collection<@Det String> coll = arList;
        new ArrayList<String>(coll);
    }

    void testConstructor2(@Det ArrayList<@Det String> arList) {
        @Det Collection<@Det String> coll = arList;
        new ArrayList<String>(coll);
    }

    void testConstructor1(@NonDet int c) {
        new ArrayList<String>(c);
    }
}
