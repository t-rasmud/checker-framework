import org.checkerframework.checker.determinism.qual.*;

public class PolyUpTest {
    @PolyDet("up") int test(@PolyDet Object arg) {
        @PolyDet("up") int x = 5;
        return x;
    }
}
