package iteration;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.checkerframework.checker.iteration.qual.HasNext;

public class TestAnonymous {
  List fields = new ArrayList();

  class MyEnumeration implements Enumeration {
    final Iterator iter = fields.iterator();

    @Override
    @SuppressWarnings("contracts.conditional.postcondition")
    public boolean hasMoreElements() {
      return iter.hasNext();
    }

    @Override
    public Object nextElement(@HasNext MyEnumeration this) {
      return iter.next();
    }
  }
}
