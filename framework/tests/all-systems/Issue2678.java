public class Issue2678 {
    @SuppressWarnings({
        "index:array.access.unsafe.low",
        "index:array.access.unsafe.high",
        "determinism:unary.increment.type.incompatible"
    })
    public synchronized void incrementPushed(long[] pushed, int operationType) {
        ++(pushed[operationType]);
    }
}
