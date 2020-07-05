import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.checkerframework.checker.determinism.qual.*;

public class Issue90 {
    void test(@OrderNonDet HashMap<@Det String, @Det String> map) {
        // :: error: assignment.type.incompatible
        for (Iterator<Entry<String, String>> entries = map.entrySet().iterator();
                // :: error: invalid.type.on.conditional
                entries.hasNext(); ) {
            // :: error: assignment.type.incompatible
            @Det Entry<@Det String, @Det String> item = entries.next();
        }
    }

    void test1(@OrderNonDet HashMap<@Det String, @Det String> map) {
        // :: error: enhancedfor.type.incompatible
        for (@Det Entry<@Det String, @Det String> item : map.entrySet()) {}
    }
}
