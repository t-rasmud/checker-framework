import org.checkerframework.checker.determinism.qual.OrderNonDet;

@SuppressWarnings("unchecked")
public class Issue759 {
    void possibleValues(final Class<? extends Enum> enumType) {
        lowercase(enumType.getEnumConstants());
    }

    <T extends Enum<T>> void lowercase(final T @OrderNonDet ... items) {}
}
