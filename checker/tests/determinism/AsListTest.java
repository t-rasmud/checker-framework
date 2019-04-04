import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class AsListTest {
    public static void f(@NonDet Integer y) {
        @Det List<@Det String> list = Arrays.<@Det String>asList("");
        @NonDet List<@NonDet Integer> intList = Arrays.<@NonDet Integer>asList(y);
    }
}
