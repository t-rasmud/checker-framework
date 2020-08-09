import java.util.Arrays;
import java.util.stream.Stream;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.OrderNonDet;

public class TestUncheckedCodeDefaults {
    void test(@Det String @OrderNonDet [] names) {
        Stream<@Det String> result = Arrays.stream(names);
    }
}
