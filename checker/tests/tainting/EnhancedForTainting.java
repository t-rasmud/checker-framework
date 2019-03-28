import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.checkerframework.checker.tainting.qual.*;

public class EnhancedForTainting {
    void test(@Tainted HashMap<@Untainted String, @Untainted String> map) {
        for (Iterator<Entry<String, String>> entries = map.entrySet().iterator();
                entries.hasNext(); ) {
            @Untainted Entry<@Untainted String, @Untainted String> item = entries.next();
        }
    }

    void test1(@Tainted HashMap<@Untainted String, @Untainted String> map) {
        for (@Untainted Entry<@Untainted String, @Untainted String> item : map.entrySet()) {}
    }
}
