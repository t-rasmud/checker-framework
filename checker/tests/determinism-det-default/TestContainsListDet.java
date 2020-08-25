import java.util.ArrayList;
import org.checkerframework.checker.determinism.qual.*;

public class TestContainsListDet {
    void TestList(@Det ArrayList<@Det Integer> myDetList, @NonDet Integer rand) {
        @Det boolean ret;
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        ret = myDetList.contains(rand);
    }

    void TestList1(
            @Det ArrayList<@Det Integer> myDetList,
            @NonDet ArrayList<@NonDet Integer> rand,
            @NonDet int randInt) {
        @Det boolean ret;
        // :: error: (assignment.type.incompatible) :: error: (method.invocation.invalid)
        ret = myDetList.contains(rand);
        // :: error: (method.invocation.invalid)
        ret = myDetList.contains(randInt);
    }

    void TestList2(@Det ArrayList<@Det Integer> myDetList, @NonDet int rand) {
        @Det boolean ret;
        // :: error: (method.invocation.invalid)
        ret = myDetList.contains(rand);
    }

    void TestList3(@NonDet int rand) {
        @SuppressWarnings("deprecation")
        // :: error: (argument.type.incompatible)
        @NonDet Integer ndInt = new Integer(rand);
    }

    void TestList4(@NonDet Integer elem) {
        @NonDet ArrayList<@NonDet Integer> arrL = new @NonDet ArrayList<@NonDet Integer>(elem);
    }

    public @PolyDet TestContainsListDet(@PolyDet int a) {}

    void TestList4(@NonDet int r) {
        @NonDet TestContainsListDet t = new TestContainsListDet(r);
    }
}
