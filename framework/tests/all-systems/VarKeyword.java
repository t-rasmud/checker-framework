import java.util.List;

// @below-java11-jdk-skip-test
public class VarKeyword {
    @SuppressWarnings("dereference.of.nullable")
    void method(List<VarKeyword> list) {
        var s = "Hello!";
        s = null;
        s.toString();
        for (var i : list) {}

        @SuppressWarnings({
            "determinism:assignment.type.incompatible",
            "determinism:argument.type.incompatible"
        }) // TODO: fix
        var listVar = list;
        method((listVar));
    }
}
