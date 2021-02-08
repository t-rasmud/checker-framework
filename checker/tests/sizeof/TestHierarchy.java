// package sizeof;
//
// import java.util.List;
// import org.checkerframework.checker.sizeof.qual.SizeOf;
//
// public class TestHierarchy {
//    List<Integer> a;
//    List<Integer> b;
//
//    void test(int x, @SizeOf("a") int y, @SizeOf("b") int z) {
//        int local = y;
//        // :: error: assignment.type.incompatible
//        y = x;
//        // :: error: assignment.type.incompatible
//        y = z;
//    }
// }
