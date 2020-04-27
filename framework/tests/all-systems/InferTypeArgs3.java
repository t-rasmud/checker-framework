import java.util.Arrays;
import java.util.HashSet;

class InferTypeArgs3 {
    @SuppressWarnings({
        "deprecation",
        "cast.unsafe.constructor.invocation",
        "determinism:type.argument.type.incompatible" // TODO: fix
    })
    void test() {
        java.util.Arrays.asList(new Integer(1), "");
    }

    @SuppressWarnings("determinism") // https://tinyurl.com/cfissue/979
    void foo() {
        new HashSet<>(Arrays.asList(new Object()));
        new HashSet<Object>(Arrays.asList(new Object())) {};
    }
}
