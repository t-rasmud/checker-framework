import org.checkerframework.checker.determinism.qual.Det;

public class P71Det {
    private @Det String getClassPrefix(Object object) {
        return object.getClass().getName()
                + "@"
                // :: error: argument.type.incompatible
                + Integer.toHexString(System.identityHashCode(object));
    }
}
