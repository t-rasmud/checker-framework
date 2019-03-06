package determinism;

import java.util.Arrays;
import org.checkerframework.checker.determinism.qual.*;

public class ArrayInits {
    void method() {
        // :: error: (invalid.upper.bound.on.type.argument)
        @Det Object @Det [] objects = new @Det Object @Det [] {Arrays.asList(1, 2, 3)};
    }
}
