import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;

public class Issue95 {
    void test(@NonDet Class<@Det String> cl, Object obj) {
        // :: error: (assignment.type.incompatible)
        @Det String cast = cl.cast(obj);
    }
}
