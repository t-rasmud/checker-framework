import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestPolyArgsRetDet {
    // No parameters, no return
    static void callee1() {}

    // One parameter, no return
    void callee2() {
        TestPolyArgsRetDet tst = this;
    }
    // two parameters, no return
    void callee3(int a) {}

    // No parameters, return
    static int callee4() {
        return 200;
    }
    // One parameter, return
    int callee5() {
        return 500;
    }
    // two parameters, return
    int callee6(int a) {
        return a;
    }
    // No receiver, one parameter, return
    static int callee7(int x) {
        return x;
    }

    static @NonDet int callee8() {
        return new Random().nextInt();
    }

    void detCaller(@Det TestPolyArgsRetDet DetObj, @Det int i) {
        TestPolyArgsRetDet.callee1();
        DetObj.callee2();
        DetObj.callee3(i);
        @Det int x = TestPolyArgsRetDet.callee4();
        @Det int y = DetObj.callee5();
        @Det int s = DetObj.callee6(y);
        @Det int a = TestPolyArgsRetDet.callee7(y);
        // :: error: (argument.type.incompatible)
        System.out.println(TestPolyArgsRetDet.callee8());
    }

    void nonDetCaller(@NonDet TestPolyArgsRetDet NonDetObj, @NonDet int i) {
        TestPolyArgsRetDet.callee1();
        // :: error: (method.invocation.invalid)
        NonDetObj.callee2();
        // :: error: (argument.type.incompatible)  :: error: (method.invocation.invalid)
        NonDetObj.callee3(i);
        @NonDet int z = TestPolyArgsRetDet.callee4();
        // :: error: (method.invocation.invalid)
        @NonDet int w = NonDetObj.callee5();
        // :: error: (method.invocation.invalid)
        @NonDet int t = NonDetObj.callee6(w);
        @NonDet int b = TestPolyArgsRetDet.callee7(w);
    }
}
