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
 * When this annotation is written without any element on a method, every parameter of that method
 * may be passed arguments such that their class's {@code toString} method returns a {@code @Det} or
 * {@code @PolyDet} result. When one or more integers are specified as annotation values, the
 * arguments at those indices must obey this restriction.
 *
 * <p>For example, if a method is annotated as {@code @RequiresDetToString()}, then at every call,
 * all arguments must have {@code toString} method return a {@code @Det} or {@code @PolyDet} result.
 * Writing {@code @RequiresDetToString({0, 2})} indicates that the first and third arguments must
 * have deterministic {@code toString} methods.
 *
 * <p>When used on an override method, the overridden method must also have
 * {@code @RequireDetToString} on the same parameters. Specifically, if the override method's
 * annotation is empty, then the overridden method's annotation must also be empty. Otherwise, for
 * each index in the override method's annotation, either the same index must appear in the
 * overridden method's annotation or the overridden method's annotation must be empty. If this rule
 * is violated, a warning is reported.
 *
 * <p>A user would typically write this annotation on formatting routines that call the {@code
 * toString} method. The formal parameters of such routines can still be annotated as
 * {@code @PolyDet}.
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