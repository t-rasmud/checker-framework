package org.checkerframework.checker.nonempty;

import java.util.HashMap;
import java.util.Map;
import org.checkerframework.dataflow.cfg.node.Node;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.flow.CFAbstractStore;
import org.checkerframework.framework.flow.CFValue;

/** Behaves just like {@link CFValue}, but additionally tracks size equalities. */
public class NonEmptyStore extends CFAbstractStore<NonEmptyValue, NonEmptyStore> {
  /** Map that stores size equalities. */
  protected static Map<String, Node> sizeEqualitiesMap;

  /**
   * NonEmptyStore constructor.
   *
   * @param analysis CFAbstractAnalysis
   * @param sequentialSemantics boolean
   */
  protected NonEmptyStore(
      CFAbstractAnalysis<NonEmptyValue, NonEmptyStore, ?> analysis, boolean sequentialSemantics) {
    super(analysis, sequentialSemantics);
  }

  /** Creates {@code sizeEqualitiesMap}. */
  protected void createSizeEqualitiesMap() {
    sizeEqualitiesMap = new HashMap<>();
  }

  /**
   * NonEmptyStore constructor.
   *
   * @param other NonEmptyStore
   */
  protected NonEmptyStore(NonEmptyStore other) {
    super(other);
  }

  /**
   * Returns the {@code sizeEqualitiesMap}.
   *
   * @return sizeEqualitiesMap
   */
  public Map<String, Node> getSizeEqualitiesMap() {
    return sizeEqualitiesMap;
  }
}
