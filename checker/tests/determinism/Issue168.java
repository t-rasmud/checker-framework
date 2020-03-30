import java.util.*;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.*;

@HasQualifierParameter(NonDet.class)
public class Issue168 {
    public static void testDetVariable(@OrderNonDet List<@Det Issue168> list) {
        // :: error: (enhancedfor.type.incompatible)
        for (@Det Issue168 s : list) {}
    }

    public static void testNonDetVariable(@OrderNonDet List<@Det Issue168> list) {
        for (@NonDet Issue168 s : list) {}
    }
}
