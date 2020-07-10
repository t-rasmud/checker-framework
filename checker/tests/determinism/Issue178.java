import org.checkerframework.checker.determinism.qual.*;

public class Issue178 {
    public static void f() {
        requiresDetToString(true ? "" : "");
    }

    @RequiresDetToString
    public static void requiresDetToString(Object o) {}
}
