import org.checkerframework.checker.determinism.qual.*;

public class InvalidArrayComponentType {

    public static void methodForName(String method) {
        @PolyDet String @PolyDet [] bnArgnames = method.split(" *, *");
        @PolyDet String @PolyDet [] argnames = bnArgnames;
    }
}
