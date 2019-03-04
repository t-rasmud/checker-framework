import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue81 {
    public static void f() {
        @Det int c = Objects.hashCode(new Object());
    }
}
