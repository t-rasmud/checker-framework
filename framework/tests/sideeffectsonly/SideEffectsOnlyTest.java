package sideeffectsonly;

import org.checkerframework.dataflow.qual.SideEffectsOnly;
import org.checkerframework.framework.qual.EnsuresQualifier;
import org.checkerframework.framework.testchecker.sideeffectsonly.qual.Refined;

public class SideEffectsOnlyTest {
  void test(Object x) {
    method(x);
    method1(x);
    method3(x);
    method2(x);
    // :: error: argument
    method3(x);
  }

  @EnsuresQualifier(
      expression = "#1",
      qualifier = org.checkerframework.framework.testchecker.sideeffectsonly.qual.Refined.class)
  // :: error: contracts.postcondition
  void method(Object x) {}

  @SideEffectsOnly({"this"})
  void method1(@Refined Object x) {}

  @SideEffectsOnly({"#1"})
  void method2(@Refined Object x) {}

  @SideEffectsOnly({"this"})
  void method3(@Refined Object x) {}
}
