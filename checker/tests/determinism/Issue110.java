import org.checkerframework.checker.determinism.qual.*;

public class Issue110 {
    public static void f(@NonDet String s) {
        // :: error: (argument.type.incompatible)
        System.out.printf("%s", s);
    }

    public static void g(@Det Object s) {
        // :: error: (nondeterministic.tostring)
        System.out.printf("%s", s);
    }

    public static void h(@Det Object s, @Det Object t) {
        // :: error: (nondeterministic.tostring)
        System.out.printf("%s, %s", s, t);
    }

    public static void testPrintln(Object obj, Issue110 obj1) {
        // :: error: (argument.type.incompatible)
        System.out.println(obj);
        // :: error: (argument.type.incompatible)
        System.out.println(obj1);
    }

    public static void testDetPrintln(@Det Issue110 obj2) {
        // :: error: (nondeterministic.tostring)
        System.out.println(obj2);
    }

    public static void testString(@Det String @Det [] str) {
        // :: error: (nondeterministic.tostring)
        System.out.println(str);
    }
}

class Sub extends Issue110 {
    @Override
    public @Det String toString() {
        return "deterministic string";
    }

    void testExtendsToString(@Det Sub obj3) {
        System.out.println(obj3);
    }

    void testExtendsToStringNull() {
        System.out.println((String) null);
    }
}

class SubSub extends Sub {
    void testExtendsToString(@Det Sub obj8) {
        System.out.println(obj8);
    }
}

class Sub1 extends Sub {
    void testExtendsToString(@Det Sub obj4) {
        System.out.println(obj4);
    }
}

class SubND extends Issue110 {
    @Override
    public @NonDet String toString() {
        return super.toString();
    }

    void testExtendsToString(@Det Sub obj4) {
        System.out.println(obj4);
    }
}

class SubSubND extends SubND {
    void testExtendsToString(@Det Sub obj6) {
        System.out.println(obj6);
    }
}

class SubPoly extends Issue110 {
    @Override
    public @PolyDet String toString() {
        return new @PolyDet String();
    }

    void testExtendsToString(@Det Sub obj5) {
        System.out.println(obj5);
    }
}

class SubSubPoly extends SubPoly {
    void testExtendsToString(@Det Sub obj7) {
        System.out.println(obj7);
    }
}
