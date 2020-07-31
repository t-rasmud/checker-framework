import org.checkerframework.checker.determinism.qual.*;

public class Issue110Det {
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

    public static void testPrintln(Object obj, Issue110Det obj1) {
        // :: error: (nondeterministic.tostring)
        System.out.println(obj);
        // :: error: (nondeterministic.tostring)
        System.out.println(obj1);
    }

    public static void testDetPrintln(@Det Issue110Det obj2) {
        // :: error: (nondeterministic.tostring)
        System.out.println(obj2);
    }

    public static void testString(@Det String @Det [] str) {
        // :: error: (nondeterministic.tostring)
        System.out.println(str);
    }
}

class SubDet extends Issue110Det {
    @Override
    // :: error: (override.receiver.invalid)
    public @Det String toString() {
        return "deterministic string";
    }

    void testExtendsToString(@Det SubDet obj3) {
        System.out.println(obj3);
    }

    void testExtendsToStringNull() {
        System.out.println((String) null);
    }
}

class SubSubDet extends SubDet {
    void testExtendsToString(@Det SubDet obj8) {
        System.out.println(obj8);
    }
}

class Sub1Det extends SubDet {
    void testExtendsToString(@Det SubDet obj4) {
        System.out.println(obj4);
    }
}

class SubNDDet extends Issue110Det {
    @Override
    // :: error: (override.receiver.invalid)
    public @NonDet String toString() {
        return super.toString();
    }

    void testExtendsToString(@Det SubDet obj4) {
        System.out.println(obj4);
    }
}

class SubSubNDDet extends SubNDDet {
    void testExtendsToString(@Det SubDet obj6) {
        System.out.println(obj6);
    }
}

class SubPolyDet extends Issue110Det {
    @Override
    // :: error: (override.receiver.invalid)
    public @PolyDet String toString() {
        return new @PolyDet String();
    }

    void testExtendsToString(@Det SubDet obj5) {
        System.out.println(obj5);
    }
}

class SubSubPolyDet extends SubPolyDet {
    void testExtendsToString(@Det SubDet obj7) {
        System.out.println(obj7);
    }
}
