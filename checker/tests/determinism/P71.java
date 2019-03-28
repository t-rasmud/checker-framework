import org.checkerframework.checker.determinism.qual.Det;

public class P71 {
    private @Det String getClassPrefix(Object object) {
        return object.getClass().getName()
                + "@"
                + Integer.toHexString(System.identityHashCode(object));
    }
}
