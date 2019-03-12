import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestMapValidity {
    void testValid1(@Det Map<@Det Integer, @Det String> map) {}

    void testValid2(@Det Map<@Det Integer, @NonDet String> map) {}

    void testValid3(@Det Map<@NonDet Integer, @Det String> map) {}

    void testValid4(@Det Map<@NonDet Integer, @NonDet String> map) {}

    void testValid5(@NonDet Map<@Det Integer, @Det String> map) {}

    void testValid6(@NonDet Map<@Det Integer, @NonDet String> map) {}

    void testValid7(@NonDet Map<@NonDet Integer, @Det String> map) {}

    void testValid8(@NonDet Map<@NonDet Integer, @NonDet String> map) {}

    // :: error: (ordernondet.on.noncollection.and.nonarray)
    void testValid9(@OrderNonDet Map<@NonDet Integer, @NonDet String> map) {}

    void testValid10(@NonDet Map<@OrderNonDet Set<@Det Integer>, @Det String> map) {}

    void testValid11(@NonDet Map<@Det String, @OrderNonDet Set<@Det Integer>> map) {}

    void testValid12(
            @NonDet Map<@OrderNonDet Set<@Det String>, @OrderNonDet Set<@Det Integer>> map) {}
}
