package org.checkerframework.checker.nonempty;

import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.checker.nonempty.qual.PolyNonEmpty;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;
import org.checkerframework.javacutil.TypesUtils;

/** Visitor for the NonEmpty Checker. */
public class NonEmptyVisitor extends BaseTypeVisitor<NonEmptyAnnotatedTypeFactory> {
  /** The java.util.Collection interface */
  private final TypeMirror collectionType =
      types.erasure(TypesUtils.typeFromClass(Collection.class, types, elements));
  /** The java.util.Iterator interface */
  private final TypeMirror iteratorType =
      types.erasure(TypesUtils.typeFromClass(Iterator.class, types, elements));
  /** The java.util.Map interface */
  private final TypeMirror mapType =
      types.erasure(TypesUtils.typeFromClass(Map.class, types, elements));
  /** The processing environment */
  ProcessingEnvironment processingEnv = atypeFactory.getProcessingEnv();
  /** The {@code Collection.size()} method */
  ExecutableElement getMethod = TreeUtils.getMethod("java.util.List", "get", 1, processingEnv);
  /** The {@literal @}{@link NonEmpty} annotation. */
  private final AnnotationMirror NONEMPTY = AnnotationBuilder.fromClass(elements, NonEmpty.class);
  /** The {@literal @}{@link PolyNonEmpty} annotation. */
  private final AnnotationMirror POLYNONEMPTY =
      AnnotationBuilder.fromClass(elements, PolyNonEmpty.class);

  /**
   * NonEmptyVisitor constructor.
   *
   * @param checker BaseTypeChecker
   */
  public NonEmptyVisitor(BaseTypeChecker checker) {
    super(checker);
  }

  /**
   * Reports error if {@code @NonEmpty} is written on primitive types.
   *
   * @param type the use of the primitive type
   * @param tree the tree where the type is used
   * @return boolean
   */
  @Override
  public boolean isValidUse(AnnotatedTypeMirror.AnnotatedPrimitiveType type, Tree tree) {
    if (type.hasAnnotation(NONEMPTY) || type.hasAnnotation(POLYNONEMPTY)) {
      return false;
    }
    return super.isValidUse(type, tree);
  }

  /**
   * Reports error if {@code @NonEmpty} is written on a declared (non-primitive) type other than
   * Collection, Iterator, Map, or their subtypes.
   *
   * @param declarationType the type of the class (TypeElement)
   * @param useType the use of the class (instance type)
   * @param tree the tree where the type is used
   * @return boolean
   */
  @Override
  public boolean isValidUse(
      AnnotatedTypeMirror.AnnotatedDeclaredType declarationType,
      AnnotatedTypeMirror.AnnotatedDeclaredType useType,
      Tree tree) {
    if (TreeUtils.isClassLiteral(tree)) {
      // Don't validate class literals
      return super.isValidUse(declarationType, useType, tree);
    }
    TypeMirror declarationTypeMirror = declarationType.getUnderlyingType();
    boolean isCollection = types.isSubtype(declarationTypeMirror, collectionType);
    boolean isIterator = types.isSubtype(declarationTypeMirror, iteratorType);
    boolean isMap = types.isSubtype(declarationTypeMirror, mapType);
    if (!isCollection && !isIterator && !isMap) {
      if (useType.hasAnnotation(NONEMPTY) || useType.hasAnnotation(POLYNONEMPTY)) {
        return false;
      }
    }

    return super.isValidUse(declarationType, useType, tree);
  }
}
