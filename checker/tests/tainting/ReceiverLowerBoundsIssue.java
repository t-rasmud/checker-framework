import org.checkerframework.checker.tainting.qual.Tainted;
import org.checkerframework.checker.tainting.qual.Untainted;

public class ReceiverLowerBoundsIssue {

    static <T> void method(@Tainted ReceiverLower<@Tainted T> arg) {}

    void testCall(@Tainted ReceiverLower<@Untainted String> arg) {
        // :: error: (argument.type.incompatible)
        method(arg);
    }

    void testCall2(@Tainted ReceiverLower<@Untainted String> arg) {
        // :: error: (method.invocation.invalid)
        arg.test();
    }
}

class ReceiverLower<T> {
    void test(@Tainted ReceiverLower<@Tainted T> this) {}
}
