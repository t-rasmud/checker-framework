package iteration;

import java.util.Iterator;
import java.util.List;

public class VerifyContracts implements Iterator {
    List<Integer> field;
    int counter = 0;

    @Override
    public boolean hasNext() {
        // :: error: contracts.conditional.postcondition.not.satisfied
        return field != null;
    }

    @Override
    public Object next() {
        int returnVal = counter;
        counter++;
        return returnVal;
    }
}
