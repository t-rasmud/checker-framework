import org.checkerframework.checker.index.qual.LowerBoundBottom;

public class InvalidBottom {
    void test(@LowerBoundBottom int x) {}
}
