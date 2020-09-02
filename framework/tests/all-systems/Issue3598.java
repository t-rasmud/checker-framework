import java.util.function.Function;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.PolyDet;

class Issue3598 {

    static class DClass extends EClass {}

    static class EClass<F> {}

    // Must be Function, can't use interface defined in this class.
    static class XClass<P> implements Function<P, P> {

        @Override
        public @PolyDet P apply(@PolyDet P protoT) {
            return protoT;
        }

        // DClass extends a raw class.
        static Function<DClass, DClass> f(@Det DClass k) {
            // Crash on this line.
            return new XClass<>(k);
        }

        XClass(P p) {}
    }
}
