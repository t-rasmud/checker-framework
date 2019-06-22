import org.checkerframework.checker.determinism.qual.*;

public class DetIssue114Defaults {

    public static void f() {
        @Det DetIssue114Defaults b = new DetIssue114Defaults();
        @Det C a = new C();
    }

    public static class C {
        void method() {
            @Det DetIssue114Defaults b = new DetIssue114Defaults();
            @Det C a = new C();
        }
    }
}
