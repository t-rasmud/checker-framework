import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.*;

public class TestPolyConstructor {
    void createArrayList(@Det int i) {
        @Det ArrayList<@Det Integer> arList = new ArrayList<Integer>(i);
        // :: error: (nondeterministic.toString)
        System.out.println(arList);
    }

    void createArrayList1(@NonDet ArrayList<@NonDet Integer> c) {
        // :: error: (argument.type.incompatible) :: warning: (cast.unsafe.constructor.invocation)
        new @Det ArrayList<Integer>(c);
    }

    void trimArrayList(@Det ArrayList<@Det String> arList) {
        arList.trimToSize();
    }
}
