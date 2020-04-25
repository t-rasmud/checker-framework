import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestCollectionTypes {
    void detDet(@Det List<@Det Integer> a) {}
    // :: error: (invalid.element.type)
    void detNd(@Det List<@NonDet Integer> a) {}

    void ondDet(@OrderNonDet List<@Det Integer> a) {}
    // :: error: (invalid.element.type)
    void ondNd(@OrderNonDet List<@NonDet Integer> a) {}
    // :: error: (invalid.element.type)
    void ndDet(@NonDet List<@Det Integer> a) {}

    void ndDd(@NonDet List<@NonDet Integer> a) {}
}
