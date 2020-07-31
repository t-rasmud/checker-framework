import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue22Det<T extends Comparable<T>> {
    public int compare(T @PolyDet [] a1, T @PolyDet [] a2) {
        // :: error: (return.type.incompatible)
        return a1[0].compareTo(a2[0]);
    }
}
