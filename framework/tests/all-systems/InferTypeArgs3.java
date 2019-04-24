import java.util.Arrays;
import java.util.HashSet;

class InferTypeArgs3 {
    void test() {
        java.util.Arrays.asList(new Integer(1), "");
    }

    @SuppressWarnings("determinism") // Warning issued because #979
    void foo() {
        new HashSet<>(Arrays.asList(new Object()));
        new HashSet<Object>(Arrays.asList(new Object())) {};
    }
}
