import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class Issue137Det {
    @RequiresDetToString
    public static void f(String... args) {}

    @RequiresDetToString
    public static void g(@Det Object er, String... args) {}

    @RequiresDetToString
    public static void i(Object... args) {}

    public static void h(@Det ErrClassDet e, Object o) {
        f("a", "b");
        // :: error: (nondeterministic.tostring)
        g(e);
        f();
    }

    public static void j(
            @NonDet CorrectClassDet a,
            @Det CorrectClassDet b,
            @NonDet ErrClassDet c,
            @Det ErrClassDet d) {
        // :: error: (argument.type.incompatible)
        i(a);
        i(b);
        // :: error: (argument.type.incompatible)
        i(a, b);
        // :: error: (argument.type.incompatible)
        i(b, a);

        // :: error: (argument.type.incompatible)
        i(c);
        // :: error: (nondeterministic.tostring)
        i(d);
        // :: error: (nondeterministic.tostring)  :: error: (argument.type.incompatible)
        i(c, d);
        // :: error: (nondeterministic.tostring)  :: error: (argument.type.incompatible)
        i(d, c);
    }
}

class ErrClassDet {
    @Override
    // :: error: (override.receiver.invalid)
    public @NonDet String toString() {
        return super.toString();
    }
}

class CorrectClassDet {
    @Override
    // :: error: (override.receiver.invalid)
    public @Det String toString() {
        return "hi";
    }
}
