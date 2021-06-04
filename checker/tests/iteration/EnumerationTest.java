package iteration;

import java.util.Enumeration;

public class EnumerationTest {
  void test(Enumeration en) {
    // :: error: method.invocation
    en.nextElement();
    Object o = en.hasMoreElements() ? en.nextElement() : null;
  }
}
