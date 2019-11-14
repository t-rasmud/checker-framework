import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class Issue137 {
    @RequiresDetToString
    public static void f(String... args) {}

    @RequiresDetToString
    public static void g(@Det Object er, String... args) {}

    @RequiresDetToString
    public static void h(@Det ErrClass e) {
        f("a", "b");
        // :: error: (nondeterministic.tostring)
        g(e);
    }
}

class ErrClass {
    @Override
    public @NonDet String toString() {
        return super.toString();
    }
}
