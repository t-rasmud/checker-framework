import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class Issue89 {
    public static void f(@Det Object @Det [] arr, @Det List<@Det Object> lst) {
        System.out.println(arr.getClass());
        Class<?> c = arr.getClass();
        System.out.println(lst.getClass());
    }

    public static void f(@Det String @Det [] @Det [] arr) {
        System.out.println(arr.getClass());
        Class<?> c = arr.getClass();
    }
}
