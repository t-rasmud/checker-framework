package iteration;

import java.util.Scanner;

public class ScannerIteration {
    void test(Scanner sc) {
        if (sc.hasNext()) {
            sc.next();
        }
        // :: error: method.invocation.invalid
        sc.next();
    }
}
