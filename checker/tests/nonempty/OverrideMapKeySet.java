package nonempty;

import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.nonempty.qual.NonEmpty;

public abstract class OverrideMapKeySet<K, V> implements Map<K, V> {
  @Override
  // :: error: override.receiver
  public @NonEmpty Set<K> keySet(@NonEmpty OverrideMapKeySet<K, V> this) {
    return keySet();
  }
}

class TestCall {
  void test(@NonEmpty Map<Integer, Integer> map) {
    @NonEmpty Set<Integer> keys = map.keySet();
  }
}
