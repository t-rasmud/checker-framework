import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

// @skip-test
public class IndexModifiers {

    void modifiers(
            @OrderNonDet List<@Det String> ondList,
            @Det List<@Det String> detList,
            @NonDet int nondetIndex) {
        // :: error: (index.modification)
        ondList.remove(0);
        // :: error: (index.modification)
        ondList.set(0, "");
        // :: error: (argument.type.incompatible)
        ondList.remove(nondetIndex);

        detList.remove(0);
        // :: error: (argument.type.incompatible)
        detList.remove(nondetIndex);
    }

    void readers(
            @OrderNonDet List<@Det String> ondList,
            @Det List<@Det String> detList,
            @NonDet int nondetIndex) {
        // :: error: (assignment.type.incompatible)
        @Det String s1 = ondList.get(0);
        @Det String s2 = detList.get(0);
        // :: error: (assignment.type.incompatible)
        @Det String s3 = ondList.get(nondetIndex);
        // :: error: (assignment.type.incompatible)
        @Det String s4 = detList.get(nondetIndex);
    }

    void addAll(
            @OrderNonDet Collection<@Det String> ondCollection,
            @Det Collection<@Det String> detCollection) {
        ondCollection.addAll(detCollection); // ok

        // :: error: (argument.type.incompatible)
        detCollection.addAll(ondCollection);

        @OrderNonDet Collection<String> ondCollection2 = detCollection;
        // :: error: (argument.type.incompatible)
        ondCollection2.addAll(ondCollection);
    }

    void specialSets(
            @OrderNonDet Collection<@Det String> ondCollection,
            @Det Collection<@Det String> detCollection) {
        @OrderNonDet HashSet<@Det String> hashSet = new HashSet<>();
        hashSet.addAll(detCollection); // ok
        @Det LinkedHashSet<@Det String> linkedHashSet = new LinkedHashSet<>();
        hashSet = linkedHashSet;
        // :: error: (argument.type.incompatible)
        hashSet.addAll(ondCollection); // error

        @Det SortedSet<@Det String> sortedSet = new TreeSet<>();
        sortedSet.addAll(ondCollection);
        sortedSet.addAll(detCollection); // ok
    }
}
