import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.*;

@HasQualifierParameter(NonDet.class)
public class ClassAssignment {
    public static void f() {
        @Det Class<@Det ClassAssignment> classObj = ClassAssignment.class;
    }
}
