package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestHashSet {
    void testConstructDefault() {
        // :: error: (assignment.type.incompatible)
        @Det Set<@Det String> s = new HashSet<String>();
    }

    void testConstructCollection1(@Det List<@Det String> c) {
        // :: error: (argument.type.incompatible)
        System.out.println(new HashSet<@Det String>(c));
    }

    void testConstructCollection2(@PolyDet List<@Det String> c) {
        // :: error: (invalid.hash.set.constructor.invocation)
        @PolyDet Set<@Det String> s = new HashSet<@Det String>(c);
    }

    void testConstructCollection3(@Det List<@Det String> c) {
        @OrderNonDet Set<@Det String> s = new HashSet<@Det String>(c);
    }

    void testConstructCollection6(@PolyDet("up") List<@Det String> c) {
        // :: error: (invalid.hash.set.constructor.invocation)
        @PolyDet("up") Set<@Det String> s = new HashSet<@Det String>(c);
    }

    void testExplicitDet() {
        // :: error: (invalid.hash.set.constructor.invocation) :: warning:
        // (cast.unsafe.constructor.invocation)
        @OrderNonDet Set<String> s = new @Det HashSet<String>();
    }

    void testExplicitPoly() {
        // :: error: (invalid.hash.set.constructor.invocation)
        new @PolyDet HashSet<String>();
    }

    void testExplicitPolyUp() {
        // :: error: (invalid.hash.set.constructor.invocation)
        new @PolyDet("up") HashSet<String>();
    }

    void testIteration() {
        @OrderNonDet Set<@Det String> s = new HashSet<String>();
        for (String str : s) {
            // :: error: (argument.type.incompatible)
            System.out.println(str);
        }
    }
}
