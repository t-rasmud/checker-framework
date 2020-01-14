import java.lang.reflect.*;
import org.checkerframework.checker.determinism.qual.*;

class Issue151 {
    public static void testNonDetClass(@NonDet Class<?> c) {
        // :: error: (assignment.type.incompatible)
        @Det String s = c.getName();
        // :: error: (assignment.type.incompatible)
        @Det Field f = c.getFields()[0];
    }

    public static void testDetClass(@Det Class<?> c) {
        @Det String s = c.getName();
        // :: error: (assignment.type.incompatible)
        @Det Field f = c.getFields()[0];
    }
}
