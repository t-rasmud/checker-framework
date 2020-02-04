import org.checkerframework.checker.determinism.qual.*;

public class Issue156 {
    public void f(String s) {}
}

class SubClass extends Issue156 {
    @RequiresDetToString
    // :: error: (invalid.requiresdettostring)
    public void f(String s) {}
}
