import java.util.List;
import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class FieldPolymorphism {
    @PolyDet Integer x;
    @PolyDet List<@PolyDet String> lst;

    void test() {
        @NonDet FieldPolymorphism obj = new @NonDet FieldPolymorphism();
        // :: error: (argument.type.incompatible)
        System.out.println(obj.x);
        // :: error: (argument.type.incompatible)
        System.out.println(obj.lst);
        @Det FieldPolymorphism obj1 = new @Det FieldPolymorphism();
        System.out.println(obj1.x + " : " + obj1.lst);
        FieldPolymorphism obj2 = new FieldPolymorphism();
        System.out.println(obj2.x + " : " + obj2.lst);
    }
}
