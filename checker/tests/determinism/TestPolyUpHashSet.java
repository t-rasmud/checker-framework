import java.util.HashSet;
import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class TestPolyUpHashSet {
    void test(@NonDet String str) {
        @NonDet HashSet<@NonDet String> set = new @NonDet HashSet<>();
        set.add(str);
    }

    void testDet(@Det String str) {
        // :: warning: (cast.unsafe.constructor.invocation)  :: error:(invalid.hashset.or.hashmap)
        @Det HashSet<@Det String> set = new @Det HashSet<>();
        set.add(str);
    }

    void test1(@PolyDet String str) {
        // :: error: (constructor.invocation.invalid)
        @PolyDet HashSet<@PolyDet("use") String> set = new @PolyDet HashSet<@PolyDet("use") String>();
        set.add(str);
    }

    void test2(@PolyDet List<@PolyDet("use") String> lst, @PolyDet("use") String str) {
        @PolyDet("upDet") HashSet<@PolyDet("use") String> set =
                new @PolyDet("upDet") HashSet<@PolyDet("use") String>();
        set.add(str);
    }
}
