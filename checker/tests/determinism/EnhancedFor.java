import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.checkerframework.checker.determinism.qual.*;

public class EnhancedFor {
    void test(@OrderNonDet HashMap<@Det String, @Det String> map) {
        for (@OrderNonDet Iterator<Entry<String, String>> entries = map.entrySet().iterator();
                entries.hasNext(); ) {
            // :: error: (assignment.type.incompatible)
            @Det Entry<@Det String, @Det String> item = entries.next();
        }
    }

    void test1(@OrderNonDet HashMap<@Det String, @Det String> map) {
        // :: error: (enhancedfor.type.incompatible)
        for (@Det Entry<@Det String, @Det String> item : map.entrySet()) {}
    }

    void testArr(@Det int @OrderNonDet [] arr) {
        // :: error: (enhancedfor.type.incompatible)
        for (@Det int x : arr) {}
    }

    void testPoly(@PolyDet List<@PolyDet String> list) {
        // :: error: (enhancedfor.type.incompatible)
        for (@PolyDet String s : list) {}
    }
}
