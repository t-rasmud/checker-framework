public class TypeVarAndArrayRefinementSmall {
    @SuppressWarnings("determinism:return.type.incompatible")
    private <T extends Enum<T>> T getEnumValue(T[] constants) {
        for (T constant : constants) {
            return constant;
        }
        throw new Error();
    }
}
