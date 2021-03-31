package iteration;

import java.util.StringTokenizer;

public class StringTokenizerTest {
    void test(String str, String delim, boolean returnDelims) {
        StringTokenizer st = new StringTokenizer(str, delim, returnDelims);
        // :: error: method.invocation.invalid
        st.nextToken();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
        }
    }

    void test1(String str, String delim, boolean returnDelims) {
        StringTokenizer st = new StringTokenizer(str, delim, returnDelims);
        // :: error: method.invocation.invalid
        st.nextElement();
        while (st.hasMoreElements()) {
            st.nextElement();
        }
    }
}
