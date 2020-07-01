import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.checksum.qual.ChecksummedBy;

public class TestLibrary {
    void testComputeChecksum(String data) {
        int checksum = ChecksumLibrary.computeChecksum(data);
    }

    void testValidateCS(Object data, int cs) {
        // :: error: (argument.type.incompatible)
        ChecksumLibrary.validateChecksum(data, cs);
    }

    void testTransformCS(
            @ChecksummedBy("#2") Object data, int cs, Function<Object, Object> lambda) {
        Map.Entry<Object, Integer> result = ChecksumLibrary.transformChecksum(data, cs, lambda);
        Object newData = result.getKey();
    }
}

class ChecksumLibrary {
    static int computeChecksum(Object data) {
        return 0;
    }

    static Object validateChecksum(@ChecksummedBy("cs") Object data, int cs) {
        return data;
    }

    static Map.Entry<Object, Integer> transformChecksum(
            @ChecksummedBy("#2") Object data, int checksum, Function<Object, Object> lambda) {
        Object oldData = validateChecksum(data);
        Object newData = lambda.apply(oldData);
        int newChecksum = computeChecksum(newData);
        return new AbstractMap.SimpleImmutableEntry<>(newData, newChecksum);
    }
}
