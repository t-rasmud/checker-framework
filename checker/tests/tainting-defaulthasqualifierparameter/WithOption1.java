package package1;

import org.checkerframework.checker.tainting.qual.*;
import org.checkerframework.framework.qual.*;

public class WithOption1 {
    @PolyTainted int field;
}
