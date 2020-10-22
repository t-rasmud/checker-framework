import java.util.ArrayList;
import java.util.Iterator;

public class TestIllegal {
    void test(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        iter.next();
    }

    void test1(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        if (!iter.hasNext()) {
            iter.next();
            iter.next();
        }
    }

    //    void test2(ArrayList<Integer> lst) {
    //        Iterator<Integer> iter = lst.iterator();
    //        while (iter.hasNext()) {
    //            iter.next();
    //            iter.next();
    //        }
    //    }

    void test3(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        if (iter.hasNext()) {
            iter.next();
            iter.next();
        }
    }
}
