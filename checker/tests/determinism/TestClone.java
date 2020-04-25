import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.*;

public class TestClone {
    void cloneArrayList(@NonDet ArrayList<@NonDet Integer> ndArList) {
        // ::error: (assignment.type.incompatible)
        @Det Object arList = ndArList.clone();
    }

    void cloneArrayList1(@OrderNonDet ArrayList<@Det Integer> ondArList) {
        Object arList = ondArList.clone();
    }
}
