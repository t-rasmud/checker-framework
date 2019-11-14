import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.*;

@HasQualifierParameter(NonDet.class)
public enum Issue138 {
    A,
    B
}
