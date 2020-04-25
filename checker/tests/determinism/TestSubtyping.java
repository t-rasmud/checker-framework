import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.*;

public class TestSubtyping {
    void TestCollection() {
        // :: error: (invalid.element.type)
        @Det ArrayList<@NonDet Integer> list = null;
    }
}
