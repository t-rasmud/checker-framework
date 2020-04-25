// @skip-test

import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.checkerframework.checker.determinism.qual.Det;

public class P18Issue {
    private void test() {
        for (@Det Method m : Logger.class.getDeclaredMethods()) {}
        @Det Method @Det [] declaredMethods = Logger.class.getDeclaredMethods();
    }
}
