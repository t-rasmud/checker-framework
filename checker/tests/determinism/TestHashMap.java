import java.util.HashMap;
import org.checkerframework.checker.determinism.qual.*;

public class TestHashMap {
    void test(@OrderNonDet HashMap<@Det Integer, @Det String> map) {
        @Det Object cl = map.clone();
        @Det Object mcl = myClone(map);
    }

    public @PolyDet Object myClone(@PolyDet HashMap<Integer, String> arg) {
        return arg;
    }
}
