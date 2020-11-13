package iteration;

import java.util.*;

public class TestRefinement {
    void testCollection(Collection<Integer> a) {
        a.add(5);
        Iterator<Integer> iter = a.iterator();
        iter.next();
    }

    void testList(List<Integer> a) {
        a.add(5);
        Iterator<Integer> iter = a.iterator();
        iter.next();
    }

    void testHashSet(HashSet<Integer> a) {
        a.add(5);
        Iterator<Integer> iter = a.iterator();
        iter.next();
    }

    void testHashMap(HashMap<Integer, Integer> a) {
        a.put(5, 5);
        Iterator<Map.Entry<Integer, Integer>> iter = a.entrySet().iterator();
        iter.next();
    }
}
