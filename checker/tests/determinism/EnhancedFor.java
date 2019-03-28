import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.checkerframework.checker.determinism.qual.*;

public class EnhancedFor {
    void test(@OrderNonDet HashMap<@Det String, @Det String> map) {
        for (Iterator<Entry<String, String>> entries = map.entrySet().iterator();
                entries.hasNext(); ) {
            @Det Entry<@Det String, @Det String> item = entries.next();
        }
    }

    void test1(@OrderNonDet HashMap<@Det String, @Det String> map) {
        for (@Det Entry<@Det String, @Det String> item : map.entrySet()) {}
    }
}
