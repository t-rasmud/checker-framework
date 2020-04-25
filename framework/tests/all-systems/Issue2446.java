class Issue2446 {
    static class One<T, V> {}

    @SuppressWarnings("super.invocation.invalid")
    static class Two<V, B extends Two<V, B>> extends One<V, B> {}

    @SuppressWarnings("super.invocation.invalid")
    static class Three<V, B extends Three<V, B>> extends Two<V, B> {}

    static <B extends Three<Object, B>> Three<Object, ?> f() {
        throw new AssertionError();
    }

    static final Three<?, ?> F = f();
}
