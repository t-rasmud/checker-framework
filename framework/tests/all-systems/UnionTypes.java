// Test case for Issue 145
// https://github.com/typetools/checker-framework/issues/145
class UnionTypes {
    @SuppressWarnings("determinism") // temporarily incorrect defaults for local variables
    public void TryCatch() {
        try {
            int[] arr = new int[10];
            arr[4] = 1;
        } catch (ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException exc) {
            Exception e = exc;
        }
    }
}
