import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.*;

@HasQualifierParameter(NonDet.class)
// :: error: (invalid.has.qual.param)
public enum Issue138 {
    A,
    B
}
