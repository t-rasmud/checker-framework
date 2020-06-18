import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue153 {
    public static void f(@Det Object o) {
        // :: error: (nondeterministic.tostring)
        System.out.print(String.format("%s", o));
    }
}
