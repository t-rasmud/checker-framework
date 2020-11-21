// Test case for https://github.com/t-rasmud/checker-framework/issues/211

// @skip-test until the bug is fixed

import java.util.HashMap;
import org.checkerframework.checker.determinism.qual.*;

public class OrderNonDetMap {

    @OrderNonDet HashMap<String, String> primitiveToFieldDescriptor = new @OrderNonDet HashMap<>(8);
}
