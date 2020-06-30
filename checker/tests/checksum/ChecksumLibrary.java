// @skip-test

import java.util.function.Function;
import javafx.util.Pair;

public class ChecksumLibrary {
    int computeChecksum(Object data) {
        return 0;
    }

    Object validateChecksum(Object data, int checksum) {
        return data;
    }

    Pair<Object, Integer> TransformChecksum(
            Object data, int checksum, Function<Object, Object> lambda) {
        Object newData = lambda.apply(data);
        return new Pair(newData, 0);
    }
}
