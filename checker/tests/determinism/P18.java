import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.checkerframework.checker.determinism.qual.Det;

public class P18 {
    private static final Set<String> EXCLUDED_METHODS =
            new HashSet<String>(Arrays.asList("getName"));

    private void invokeMethods(Logger proxyLogger)
            throws InvocationTargetException, IllegalAccessException {
        for (@Det Method m : Logger.class.getDeclaredMethods()) {
            if (!EXCLUDED_METHODS.contains(m.getName())) {
                m.invoke(proxyLogger, new Object[m.getParameterTypes().length]);
            }
        }
        @Det Method @Det [] declaredMethods = Logger.class.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            @Det Method m = declaredMethods[i];
        }
    }
}
