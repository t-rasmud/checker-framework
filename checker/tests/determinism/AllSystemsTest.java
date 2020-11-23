import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.framework.qual.HasQualifierParameter;

interface AllSystemsMMyMap<KEY, VALUE> {}

interface AllSystemsMMyList<LL> {}

@HasQualifierParameter(NonDet.class)
public class AllSystemsTest<KK extends FF, FF extends AllSystemsMMyMap<KK, KK>> {
    KK kk;
    FF ff;

    AllSystemsTest(KK kk, FF ff) {
        this.kk = kk;
        this.ff = ff;
    }
}

class AllSystemsRecursiveTypevarClass<T extends AllSystemsRecursiveTypevarClass<T>> {
    T t;

    AllSystemsRecursiveTypevarClass(T t) {
        // :: error: invalid.field.assignment
        this.t = t;
    }
}

class AllSystemsRecursiveImplements implements AllSystemsMMyList<AllSystemsRecursiveImplements> {}
