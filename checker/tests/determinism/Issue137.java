import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class Issue137 {
    @RequiresDetToString
    public static void f(String... args) {}

    @RequiresDetToString
    public static void g(@Det Object er, String... args) {}

    @RequiresDetToString
    public static void i(Object... args) {}

    public static void h(@Det ErrClass e, Object o) {
        f("a", "b");
        // :: error: (nondeterministic.tostring)
        g(e);
        f();
    }

    public static void j(
            @NonDet CorrectClass a, @Det CorrectClass b, @NonDet ErrClass c, @Det ErrClass d) {
        i(a);
        i(b);
        i(a, b);
        i(b, a);

        i(c);
        // :: error: (nondeterministic.tostring)
        i(d);
        // :: error: (nondeterministic.tostring)
        i(c, d);
        // :: error: (nondeterministic.tostring)
        i(d, c);
    }
}

class ErrClass {
    @Override
    public @NonDet String toString() {
        return super.toString();
    }
}

class CorrectClass {
    @Override
    public @Det String toString() {
        return "hi";
    }
}
