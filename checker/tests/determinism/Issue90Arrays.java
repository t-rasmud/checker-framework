import java.lang.reflect.Method;
import java.util.logging.Logger;
import org.checkerframework.checker.determinism.qual.Det;

public class Issue90Arrays {
    private void test() {
        // :: error: (enhancedfor.type.incompatible)
        for (@Det Method m : Logger.class.getDeclaredMethods()) {}
        // :: error: (assignment.type.incompatible)
        @Det Method @Det [] declaredMethods = Logger.class.getDeclaredMethods();
    }
}
