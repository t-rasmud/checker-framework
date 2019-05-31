package determinism;

public class TryCatchException {
    void bar() {
        try {
            throw new Throwable();
        } catch (RuntimeException ex) {
            // :: error: (nondeterministic.toString)
            System.out.println(ex);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
