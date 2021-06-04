package nonempty;

import java.util.ArrayList;
import java.util.List;

public class SizeOfFieldLoop {
  List<Integer> lst = new ArrayList<>();

  void test() {
    for (int i = 0; i < lst.size(); i++) {
      lst.get(i);
    }
  }

  void test1(ArrayList<Integer> aList) {
    for (int i = 0; i < aList.size(); i++) {
      aList.get(i);
    }
  }
}
