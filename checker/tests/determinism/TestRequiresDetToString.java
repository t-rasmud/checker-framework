import org.checkerframework.checker.determinism.qual.*;

public class TestRequiresDetToString {
    public void printObject(@Det Object o) {
        // :: error: (nondeterministic.tostring)
        System.out.println(o);
    }

    public void printNonObject(@Det TestRequiresDetToString o) {
        // :: error: (nondeterministic.tostring)
        System.out.println(o);
    }

    public void testPositional(@Det String det, @Det Object nonDet) {
        onlyFirstRequired(det, nonDet);
        // :: error: (nondeterministic.tostring)
        onlyFirstRequired(nonDet, det);
        // :: error: (nondeterministic.tostring)
        onlySecondRequired(det, nonDet);
        onlySecondRequired(nonDet, det);

        // :: error: (nondeterministic.tostring)
        bothRequired(nonDet, det);
        // :: error: (nondeterministic.tostring)
        bothRequired(det, nonDet);
    }

    public void testVarArgs(@Det Object o) {
        // :: error: (nondeterministic.tostring)
        varArgs1("", o);
        // :: error: (nondeterministic.tostring)
        varArgs2("", o);
    }

    @RequiresDetToString(0)
    public void onlyFirstRequired(Object o1, Object o2) {}

    @RequiresDetToString(1)
    public void onlySecondRequired(Object o1, Object o2) {}

    @RequiresDetToString({0, 1})
    public void bothRequired(Object o1, Object o2) {}

    @RequiresDetToString(0)
    public void varArgs1(Object... args) {}

    @RequiresDetToString
    public void varArgs2(Object... args) {}

    public void method1() {}

    @RequiresDetToString(0)
    public void method2() {}

    @RequiresDetToString(1)
    public void method3() {}

    @RequiresDetToString
    public void method4() {}

    public static class Override1 extends TestRequiresDetToString {
        @RequiresDetToString
        // :: error: (invalid.requiresdettostring)
        public void method1() {}

        @RequiresDetToString
        // :: error: (invalid.requiresdettostring)
        public void method2() {}

        @RequiresDetToString(0)
        // :: error: (invalid.requiresdettostring)
        public void method3() {}

        @RequiresDetToString
        public void method4() {}
    }

    public static class Override2 extends TestRequiresDetToString {
        @RequiresDetToString(0)
        // :: error: (invalid.requiresdettostring)
        public void method1() {}

        @RequiresDetToString(0)
        public void method2() {}

        @RequiresDetToString(0)
        public void method4() {}
    }
}
