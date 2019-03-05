package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

class TestLinkedHashSet {
    void testEmptyConstructor() {
        @Det LinkedHashSet<String> s = new LinkedHashSet<>();
        System.out.println(s);
    }

    void testCollectionConstructor(@Det List<String> list) {
        @Det LinkedHashSet<String> s = new LinkedHashSet<>(list);
        System.out.println(s);
    }

    void testIteration(@Det List<String> list) {
        @Det LinkedHashSet<String> s = new LinkedHashSet<>(list);
        for (String str : s) {
            System.out.println(str);
        }
    }
}
