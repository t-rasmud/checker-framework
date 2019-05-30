import org.checkerframework.checker.determinism.qual.*;

public class Issue110 {
    public static void f(@NonDet String s) {
        // :: error: (argument.type.incompatible)
        System.out.printf("%s", s);
    }

    public static void g(@Det Object s) {
        // :: error: (nondeterministic.toString)
        System.out.printf("%s", s);
    }

    public static void testPrintln(Object obj, Issue110 obj1) {
        // :: error: (argument.type.incompatible)
        System.out.println(obj);
        // :: error: (argument.type.incompatible)
        System.out.println(obj1);
    }

    public static void testDetPrintln(@Det Issue110 obj2) {
        // :: error: (nondeterministic.toString)
        System.out.println(obj2);
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
}

class Sub1 extends Sub {
    void testExtendsToString(@Det Sub obj4) {
        System.out.println(obj4);
    }
}
