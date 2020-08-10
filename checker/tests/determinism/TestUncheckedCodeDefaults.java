import java.util.Arrays;
import java.util.Optional;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class TestUncheckedCodeDefaults {
    void test(@Det String @OrderNonDet [] names, String attributeName) {
        final Optional<String> result =
                Arrays.stream(names).filter(name -> name.equals(attributeName)).findFirst();
    }
}
