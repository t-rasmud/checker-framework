import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue123<T extends @NonDet Object> {
    public class C {
        public C(T a) {}

        public void f(T a) {
            new C(a);
        }
    }
}
