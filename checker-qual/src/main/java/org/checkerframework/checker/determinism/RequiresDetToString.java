package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.InheritedAnnotation;

// TODO: The use of 0-based indexing is very confusing because the Checker Framework uses 1-based
// indexing for formal parameters:
// https://checkerframework.org/manual/#java-expressions-as-arguments .
// It ought to be changed to 1-based indexing.
/**
 * A method with this annotation, for each specified parameter, may only be passed arguments where
 * the class's {@code toString} method returns a {@code Det} or {@code PolyDet} result. When written
 * with no arguments, every parameter must obey this restriction. When one or more integers are
 * specified, only the parameters at those indices are checked.
 *
 * <p>For example, {@code @RequiresDetToString} on its own indicates that every argument must have a
 * deterministic {@code toString} method. Writing {@code @RequiresDetToString({0, 2})} indicates
 * that only the first and third arguments must have deterministic {@code toString} methods.
 *
 * <p>When used on an override method, the overridden method must also have
 * {@code @RequireDetToString} on the same parameters. Specifically, if the override method's
 * annotation is empty, then the overridden method's annotation must also be empty. Otherwise, for
 * each index in the override method's annotation, either the same index must appear in the
 * overridden method's annotation or the overridden method's annotation must be empty. If this rule
 * is violated, a warning is reported.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@InheritedAnnotation
@Target({ElementType.METHOD})
public @interface RequiresDetToString {
    /**
     * Parameters that are required to have deterministic {@code toString} methods.
     *
     * @return default value
     */
    int[] value() default {};
}
