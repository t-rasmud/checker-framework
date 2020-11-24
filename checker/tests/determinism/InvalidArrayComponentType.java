// Test case for https://github.com/t-rasmud/checker-framework/issues/213

// @skip-test until the issue is fixed

import org.checkerframework.checker.determinism.qual.*;

public class InvalidArrayComponentType {

    public static void methodForName(String method) {
        @PolyDet String @PolyDet [] bnArgnames = method.split(" *, *");
        @PolyDet String @PolyDet [] argnames = bnArgnames;
    }
}
