import java.util.List;
import org.checkerframework.checker.tainting.qual.PolyTainted;
import org.checkerframework.checker.tainting.qual.Tainted;
import org.checkerframework.checker.tainting.qual.Untainted;

public class TaintingFieldPolymorphism {
    @PolyTainted Integer x;
    @PolyTainted List<@PolyTainted String> lst;

    void test() {
        @Tainted TaintingFieldPolymorphism obj = new @Tainted TaintingFieldPolymorphism();
        // :: error: (assignment.type.incompatible)
        @Untainted Integer myX = obj.x;
        // :: error: (assignment.type.incompatible)
        @Untainted List<@Untainted String> myLst = obj.lst;
        // :: warning: (cast.unsafe.constructor.invocation)
        @Untainted TaintingFieldPolymorphism obj1 = new @Untainted TaintingFieldPolymorphism();
        @Untainted Integer myX1 = obj1.x;
        TaintingFieldPolymorphism obj2 = new TaintingFieldPolymorphism();
        // :: error: (assignment.type.incompatible)
        @Untainted List<@Untainted String> myLst2 = obj2.lst;
    }
}
