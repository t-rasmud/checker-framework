// test case for https://github.com/t-rasmud/checker-framework/issues/182

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue182 {
    public Issue182(List<@PolyDet String> list) {
        @PolyDet List<@PolyDet String> copy = list;
    }

    public Issue182(String[] arr) {
        @PolyDet String @PolyDet [] arr2 = arr;
    }
}
