package iteration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.iteration.qual.HasNext;

public class ClosureFP1 {
    private class NeighborIterator implements Iterator {

        List<Integer> inEdgeList = new ArrayList<>();
        List<Integer> outEdgeList = new ArrayList();

        private final Iterator<Integer> in = inEdgeList.iterator();
        private final Iterator<Integer> out = outEdgeList.iterator();

        @Override
        @SuppressWarnings("contracts.conditional.postcondition.not.satisfied")
        public boolean hasNext() {
            return in.hasNext() || out.hasNext();
        }

        @Override
        public Integer next(@HasNext NeighborIterator this) {
            boolean isOut = !in.hasNext();
            Iterator<Integer> curIterator = isOut ? out : in;
            Integer s = curIterator.next();
            return isOut ? s : 0;
        }
    }
}
