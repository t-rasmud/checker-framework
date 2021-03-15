package nonempty;

@org.checkerframework.framework.qual.HasQualifierParameter(
        org.checkerframework.checker.nonempty.qual.UnknownNonEmpty.class)
@SuppressWarnings({"nonempty:super.invocation.invalid", "nonempty:inconsistent.constructor.type"})
public class OverrideToString {
    @Override
    public String toString(OverrideToString this) {
        return super.toString();
    }
}
