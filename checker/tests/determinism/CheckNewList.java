package determinism;

import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class CheckNewList {
    void newList() {
        @Det List<@Det Integer> lst = new ArrayList<Integer>(new ArrayList<Integer>());
    }
}
