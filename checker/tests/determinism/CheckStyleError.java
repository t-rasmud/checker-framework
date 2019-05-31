import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.jar.Attributes;
import org.checkerframework.checker.determinism.qual.Det;

public class CheckStyleError {
    public void testIncorrectTag(@Det Class<?> arg) throws Exception {
        try {
            final @Det Class<?> aClassParent = arg;
            Constructor<?> ctorParent = null;
            final @Det Constructor<?> @Det [] parentConstructors =
                    // :: error: (assignment.type.incompatible)
                    aClassParent.getDeclaredConstructors();
            for (@Det Constructor<?> parentConstructor : parentConstructors) {
                parentConstructor.setAccessible(true);
                ctorParent = parentConstructor;
            }
            final Class<?> aClass =
                    Class.forName(
                            "com.puppycrawl.tools.checkstyle."
                                    + "ConfigurationLoader$InternalLoader");
            Constructor<?> constructor = null;
            final Constructor<?>[] constructors = aClass.getDeclaredConstructors();
            for (Constructor<?> constr : constructors) {
                constr.setAccessible(true);
                constructor = constr;
            }

            final Object objParent = ctorParent.newInstance(null, true);
            final Object obj = constructor.newInstance(objParent);

            final Class<?>[] param =
                    // :: error: (invalid.array.component.type)
                    new Class<?>[] {
                        String.class, String.class, String.class, Attributes.class,
                    };
            // :: error: (invalid.array.component.type)
            final Method method = aClass.getDeclaredMethod("startElement", param);

            method.invoke(obj, "", "", "hello", null);

        } catch (InvocationTargetException ex) {
            // :: error: (nondeterministic.toString)
            System.out.println(ex.getCause());
        }
    }
}
