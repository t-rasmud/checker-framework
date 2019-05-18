// Test case for Issue 2199.
@SuppressWarnings("unchecked")
class Issue2199 {
    static class StrangeConstructorTypeArgs<K, V> {
        public StrangeConstructorTypeArgs(Abstract<String, byte[]> abs) {}
    }

    abstract static class Abstract<KEY, VALUE> {}

    @SuppressWarnings("super.invocation.invalid")
    static class Concrete<K, V> extends Abstract<K, V> {}

    @SuppressWarnings("return.type.incompatible")
    static StrangeConstructorTypeArgs getStrangeConstructorTypeArgs() {
        return new StrangeConstructorTypeArgs(new Concrete<>());
    }
}
