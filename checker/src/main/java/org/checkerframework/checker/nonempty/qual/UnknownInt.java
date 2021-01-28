package org.checkerframework.checker.nonempty.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.*;

/**
 * Indicates that nothing is known about the value of the int. Used by the NonEmpty Checker to check
 * that the size of a Collection.
 *
 * <p>Used internally by the type system; should never be written by a programmer.
 *
 * @checker_framework.manual #nonempty-checker NonEmpty Checker
 */
@InvisibleQualifier
@SubtypeOf({})
@DefaultQualifierInHierarchy
@DefaultFor({TypeUseLocation.LOWER_BOUND})
@QualifierForLiterals(LiteralKind.NULL)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@RelevantJavaTypes(int.class)
public @interface UnknownInt {}
