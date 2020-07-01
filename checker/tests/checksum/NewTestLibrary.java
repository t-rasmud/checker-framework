import java.util.function.Function;

public class NewTestLibrary {
    void test(String data, Function<String, String> lambda) {
        Checksummed<String> checksummed = new Checksummed<String>(data);
        // Is this legal?
        System.out.println(checksummed);
        // Is this legal?
        String mutated = data + "hey";
        Checksummed<String> newChecksummed = checksummed.transformChecksum(lambda);
        newChecksummed.validateChecksum();
    }
}

class Checksummed<T> {
    private T data;
    private int checksum;

    // Only way to compute checksum
    public Checksummed(T data) {
        this.data = data;
        this.checksum = 0;
    }

    Checksummed<T> transformChecksum(Function<T, T> lambda) {
        this.validateChecksum();
        T newData = lambda.apply(this.data);
        return new Checksummed(newData);
    }

    // Is the return type correct?
    T validateChecksum() {
        // checksum validation code omitted here.
        return this.data;
    }
}
