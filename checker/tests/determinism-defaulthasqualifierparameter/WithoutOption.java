import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.*;

public class WithoutOption {
    // :: error: (invalid.polymorphic.qualifier.use)
    @PolyDet int field;
}
