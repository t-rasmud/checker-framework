import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import org.checkerframework.checker.determinism.qual.*;

public class TestMaps {
    void testMapCreation() {
        @OrderNonDet HashMap<@Det Integer, @Det String> hashMap = new HashMap<>();
        @Det TreeMap<@Det Integer, @Det String> treeMap = new TreeMap<>(hashMap);
        @Det LinkedHashMap<@Det String, @Det List<@Det Integer>> lMap = new LinkedHashMap<>();
    }

    void testMapGet(
            @OrderNonDet Map<@Det Integer, @Det String> map,
            @OrderNonDet Map<@Det Integer, @OrderNonDet List<@Det String>> map1,
            @Det Integer dKey,
            @NonDet Integer nKey) {
        @Det String val = map.get(dKey);
        // :: error: (assignment.type.incompatible)
        @Det List<@Det String> val1 = map1.get(dKey);
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        @Det String val2 = map.get(nKey);
    }

    void testHashMapGet(
            @OrderNonDet HashMap<@Det Integer, @Det String> map,
            @OrderNonDet HashMap<@Det Integer, @Det List<@Det String>> map1,
            @Det Integer dKey,
            @NonDet Integer nKey) {
        @Det String val = map.get(dKey);
        @Det List<@Det String> val1 = map1.get(dKey);
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        @Det String val2 = map.get(nKey);
    }

    void testMapSize(
            @OrderNonDet Map<@Det Integer, @Det String> map,
            @Det Map<@Det Integer, @Det String> map1,
            @NonDet Map<@NonDet Integer, @NonDet String> map2) {
        @Det int sz = map.size();
        @Det int sz1 = map1.size();
        // :: error: (assignment.type.incompatible)
        @Det int sz2 = map2.size();
    }

    void testLinkedHashMapSet(
            @OrderNonDet LinkedHashMap<@Det Integer, @Det String> map,
            @Det LinkedHashMap<@Det Integer, @Det String> map1,
            @NonDet LinkedHashMap<@NonDet Integer, @NonDet String> map2) {
        @Det int sz = map.size();
        @Det int sz1 = map1.size();
        // :: error: (assignment.type.incompatible)
        @Det int sz2 = map2.size();
    }

    void testMapPut(@Det Map<@Det Integer, @Det String> map, @Det Integer key, @NonDet String val) {
        map.put(key, "hi");
        // :: error: (argument.type.incompatible)
        map.put(key, val);
    }

    void testTreeMapPut(
            @Det TreeMap<@Det Integer, @Det String> map, @Det Integer key, @NonDet String val) {
        map.put(key, "hi");
        // :: error: (argument.type.incompatible)
        map.put(key, val);
    }

    void testTreeMapIteration() {
        @OrderNonDet HashMap<@Det Integer, @Det String> hashMap = new HashMap<>();
        @Det TreeMap<@Det Integer, @Det String> treeMap = new TreeMap<>(hashMap);
        @Det Set<Entry<Integer, String>> it = treeMap.entrySet();
    }

    void testHashMapIteration() {
        @OrderNonDet HashMap<@Det Integer, @Det String> hashMap = new HashMap<>();
        // :: error: (assignment.type.incompatible)
        @Det Set<Entry<Integer, String>> it = hashMap.entrySet();
    }

    void testLinkedHashMapIteration() {
        @OrderNonDet LinkedHashMap<@Det Integer, @Det String> hashMap = new @OrderNonDet LinkedHashMap<>();
        // :: error: (assignment.type.incompatible)
        @Det Set<Integer> kSet = hashMap.keySet();
    }

    void testToString(@Det Map<Integer, String> map, @Det TreeMap<Integer, String> map1) {
        // :: error: (assignment.type.incompatible)
        @Det Object str = map.toString();
        // :: error: (assignment.type.incompatible)
        @Det Object str1 = map1.toString();
    }
}
