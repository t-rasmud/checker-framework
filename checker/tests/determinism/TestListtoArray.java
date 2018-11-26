import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestListtoArray {
    void ListToObjectArray(@Det List<@Det String> detList) {
        @Det Object @Det [] objArr = detList.toArray();
    }

    void ListToObjectArray1(@OrderNonDet List<@Det String> ondetList) {
        @NonDet Object @NonDet [] objArr = ondetList.toArray();
    }

    void ListToObjectArray2(@Det List<@Det String> detList) {
        @Det String @Det [] objArr = detList.toArray(new @Det String @Det [10]);
    }

    void ListToObjectArray4(@OrderNonDet List<@Det String> ondetList) {
        @Det String @OrderNonDet [] arg = new @Det String @OrderNonDet [10];
        @NonDet String @NonDet [] objArr = ondetList.toArray(arg);
        @NonDet String @NonDet [] objArr1 = ondetList.toArray(new @NonDet String @NonDet [10]);
    }
}
