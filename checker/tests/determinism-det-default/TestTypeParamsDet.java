import org.checkerframework.checker.determinism.qual.*;

class MyClassDet<T> {
    T data;

    MyClassDet() {}

    MyClassDet(T data) {
        this.data = data;
    }

    MyClassDet(int x) {}
}

public class TestTypeParamsDet {
    void testtypes(@Det int a, @NonDet int y) {
        MyClassDet<Integer> obj = new MyClassDet<Integer>(a);
        // :: error: (nondeterministic.tostring)
        System.out.println(obj);
        MyClassDet<String> sobj = new MyClassDet<String>();
        // :: error: (nondeterministic.tostring)
        System.out.println(sobj);
        // :: error: (argument.type.incompatible)
        MyClassDet<Integer> nobj = new MyClassDet<Integer>(y);
        // :: error: (nondeterministic.tostring)
        System.out.println(nobj);
    }
}
