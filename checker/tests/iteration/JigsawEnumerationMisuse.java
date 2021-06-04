package iteration;

import java.util.Enumeration;

public class JigsawEnumerationMisuse {
  public void getLocale(Enumeration enumeration) {
    // :: error: method.invocation
    enumeration.nextElement();
  }
}
