import org.checkerframework.checker.determinism.qual.*;

public class Issue174 {
    static void testGetInterfacesDet(@Det Class<?> c) {
        @Det Class<?> @Det [] interfaces = c.getInterfaces();
    }

    static void testGetInterfacesNonDet(@NonDet Class<?> c) {
        // :: error: (assignment.type.incompatible)
        @Det Class<?> @Det [] interfaces = c.getInterfaces();
    }
}
