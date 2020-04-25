import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public @NonDet class Issue57Correct<T extends @NonDet Object> implements @NonDet Iterator<T> {
    public Issue57Correct() {}

    public @PolyDet("down") boolean hasNext() {
        return false;
    }

    public T next() {
        return null;
    }
}
