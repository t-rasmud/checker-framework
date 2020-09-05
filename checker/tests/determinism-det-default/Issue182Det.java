// test case for https://github.com/t-rasmud/checker-framework/issues/182

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue182Det {
    // :: error: (invalid.element.type)
    public Issue182Det(List<@PolyDet String> list) {
        // :: error: (invalid.element.type)  :: error: (assignment.type.incompatible)
        @PolyDet List<@PolyDet String> copy = list;
    }

    public Issue182Det(String[] arr) {
        @PolyDet String @PolyDet [] arr2 = arr;
    }
}
