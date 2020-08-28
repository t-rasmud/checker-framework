import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue193 {
    public <T extends @Det String> T callMapGet(
            @OrderNonDet Map<@Det String, T> map, @Det String key) {
        return map.get(key);
    }
}
