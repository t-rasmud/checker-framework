// @skip-test Issue#165
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;

public class ReceiverLowerBoundIssue {

    static <T> void method(@NonDet ReceiverLower<@NonDet T> arg) {}

    void testCall(@NonDet ReceiverLower<@Det String> arg) {
        // :: error: (argument.type.incompatible)
        method(arg);
    }

    void testCallInvariant(@NonDet ReceiverLower<@Det String> arg) {
        // :: error: (method.invocation.invalid)
        arg.test();
    }
}

class ReceiverLower<T> {
    void test(@NonDet ReceiverLower<@NonDet T> this) {}
}
