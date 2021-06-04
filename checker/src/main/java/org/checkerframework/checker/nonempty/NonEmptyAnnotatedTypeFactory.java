package org.checkerframework.checker.nonempty;

import java.util.List;
import javax.lang.model.element.VariableElement;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.flow.CFAbstractAnalysis;
import org.checkerframework.framework.type.GenericAnnotatedTypeFactory;
import org.checkerframework.javacutil.Pair;

/** The annotated type factory for the NonEmpty type system. */
public class NonEmptyAnnotatedTypeFactory
    extends GenericAnnotatedTypeFactory<
        NonEmptyValue, NonEmptyStore, NonEmptyTransfer, NonEmptyAnalysis> {

  /**
   * Constructor for NonEmptyAnnotatedTypeFactory.
   *
   * @param checker BaseTypeChecker
   */
  public NonEmptyAnnotatedTypeFactory(BaseTypeChecker checker) {
    super(checker);
    this.postInit();
  }

  @Override
  protected NonEmptyAnalysis createFlowAnalysis(
      List<Pair<VariableElement, NonEmptyValue>> fieldValues) {
    return new NonEmptyAnalysis(checker, this, fieldValues);
  }

  @Override
  public NonEmptyTransfer createFlowTransferFunction(
      CFAbstractAnalysis<NonEmptyValue, NonEmptyStore, NonEmptyTransfer> analysis) {
    return new NonEmptyTransfer((NonEmptyAnalysis) analysis);
  }
}