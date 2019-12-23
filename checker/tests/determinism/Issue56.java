import org.checkerframework.checker.determinism.qual.*;

// :: error: declaration.inconsistent.with.extends.clause
public class Issue56 extends @Det Object {
    public static void f(@NonDet Issue56 a) {}
}
