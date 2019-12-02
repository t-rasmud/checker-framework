package package2;

import org.checkerframework.checker.tainting.qual.*;
import org.checkerframework.framework.qual.*;

public class WithOption2 {
    @PolyTainted int field;
}
