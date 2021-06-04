// Test case for Issue 1218:
// https://github.com/typetools/checker-framework/issues/1218

import java.io.Serializable;
import org.checkerframework.common.value.qual.*;

public class Issue1218 {

  enum MyEnum {
    A,
    B,
    C;
  }

  class ForString {
    ForString(String @MinLen(2) ... strs) {}
  }

  class ForInt {
    ForInt(@IntVal({1, 2, 3}) int @MinLen(2) ... strs) {}
  }

  class ForEnum<E extends Enum<E>> {
    @SafeVarargs
    ForEnum(E @MinLen(2) ... enums) {}
  }

  class ForAny<T> {
    @SafeVarargs
    ForAny(T @MinLen(3) ... anys) {}
  }

  void strs(String @MinLen(2) ... strs) {}

  void ints(@IntVal({1, 2, 3}) int @MinLen(2) ... ints) {}

  @SafeVarargs
  final <E extends Enum<E>> void enums(E @MinLen(2) ... enums) {}

  @SafeVarargs
  final <T> void anys(T @MinLen(3) ... anys) {}

  void testMethodCall() {
    // :: error: (varargs)
    strs();
    // :: error: (varargs)
    strs("");
    strs("", "");
    // type of arg should be @UnknownVal String @BottomVal []
    strs((String[]) null);

    String[] args0 = {""};
    String[] args1 = {""};
    String[] args2 = {"", ""};

    // :: error: (argument)
    strs(args0);
    // :: error: (argument)
    strs(args1);
    strs(args2);

    ints(1, 2);
    // :: error: (argument)
    ints(0, 0, 0);
    // :: error: (varargs)
    ints(3);
    // type of arg should be @IntVal(1) int @BottomVal []
    ints((@IntVal(1) int[]) null);
  }

  // Inferred enumval types are incompatible with <E extends Enum<E>>. Similar code
  // works if the type is a specific enum; see the test file Enums.java for an example.
  @SuppressWarnings("type.argument")
  void testMethodCallTypeInferred() {
    // :: error: (varargs)
    enums();
    // :: error: (varargs)
    enums(MyEnum.A);
    enums(MyEnum.A, MyEnum.B);
    enums(MyEnum.A, MyEnum.B, MyEnum.C);
  }

  <T extends Comparable<T> & Serializable> void testMethodCallTypeInferredIntersection() {
    T t = null;

    // :: error: (varargs)
    anys(1, 1.0);
    // :: error: (varargs)
    anys(1, "");
    anys(1, 1.0, "");
    // :: error: (varargs)
    anys(1, t);
    anys(1, t, "");
  }

  void testConstructorCall() {
    // :: error: (varargs)
    new ForString();
    // :: error: (varargs)
    new ForString("");
    new ForString("", "");
    // type of arg should be @UnknownVal String @BottomVal []
    new ForString((String[]) null);

    String[] args0 = {""};
    String[] args1 = {""};
    String[] args2 = {"", ""};

    // :: error: (argument)
    new ForString(args0);
    // :: error: (argument)
    new ForString(args1);
    new ForString(args2);

    new ForInt(1, 2);
    // :: error: (argument)
    new ForInt(0, 0, 0);
    // :: error: (varargs)
    new ForInt(3);
    // type of arg should be @IntVal(1) int @BottomVal []
    ints((@IntVal(1) int[]) null);
  }

  void testConstructorCallTypeInferred() {
    // :: error: (varargs)
    new ForEnum<>(MyEnum.A);
    new ForEnum<>(MyEnum.A, MyEnum.B);
    new ForEnum<>(MyEnum.A, MyEnum.B, MyEnum.C);
  }

  @SuppressWarnings("unchecked")
  <T extends Comparable<T> & Serializable> void testConstructorCallTypeInferredIntersection() {
    T t = null;

    // :: error: (varargs)
    new ForAny<>(1, 1.0);
    // :: error: (varargs)
    new ForAny<>(1, "");
    new ForAny<>(1, 1.0, "");
    // :: error: (varargs)
    new ForAny<>(1, t);
    new ForAny<>(1, t, "");
  }
}
