package iteration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.checkerframework.dataflow.qual.SideEffectsOnly;

public class TestLegalLoops {
    void testWhile(ArrayList<Integer> lst) {
        Iterator<Integer> iter = lst.iterator();
        while (iter.hasNext()) {
            iter.next();
        }
    }

    void testFor(ArrayList<Integer> lst) {
        for (Iterator<Integer> iter = lst.iterator(); iter.hasNext(); ) {
            iter.next();
        }
    }

    void testForEach(ArrayList<Integer> lst) {
        for (Integer elem : lst) {}
    }

    @SideEffectsOnly("sb")
    public void toJSONString(Map map) {

        StringBuffer sb = new StringBuffer();
        Iterator iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            sb.append(',');
            Map.Entry entry = (Map.Entry) iter.next();
        }
    }
}
