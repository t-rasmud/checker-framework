package sideeffectsonly;

import java.util.Collection;
import org.checkerframework.dataflow.qual.SideEffectsOnly;

public class IncorrectSideEffectsOnly {
  Collection<Integer> coll;

  @SideEffectsOnly({"#2"})
  void test(Collection<Integer> cl, Collection<Integer> cl2) {
    cl.add(9);
  }

  @SideEffectsOnly({"#2"})
  void test1(Collection<Integer> cl, Collection<Integer> cl2) {
    // :: error: incorrect.sideeffectsonly
    coll = cl;
  }
}
