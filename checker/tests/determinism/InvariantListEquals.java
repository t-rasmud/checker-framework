import java.util.List;
import java.util.Set;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;

public class InvariantListEquals {
    void testOtherType(@NonDet List<@Det String> lst, @NonDet Set<@Det String> st) {
        @Det boolean result = lst.equals(st);
    }
}
