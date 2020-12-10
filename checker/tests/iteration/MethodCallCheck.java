package iteration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.dataflow.qual.Deterministic;

public class MethodCallCheck {
    List<Integer> children = new ArrayList<>();

    void testIf() {
        if (childIterator().hasNext()) {
            childIterator().next();
        }
    }

    void testAgain() {
        Iterator<Integer> iter = childIterator();
        if (iter.hasNext()) {
            iter.next();
        }
    }

    @Deterministic
    public Iterator<Integer> childIterator() {
        return children.iterator();
    }
}
