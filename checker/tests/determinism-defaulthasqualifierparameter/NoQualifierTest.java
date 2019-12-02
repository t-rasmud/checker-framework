package package1;

import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.*;

@NoQualifierParameter(NonDet.class)
public class NoQualifierTest {
    // :: error: (invalid.polymorphic.qualifier.use)
    @PolyDet int field;
}
