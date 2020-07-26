package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.InheritedAnnotation;

/**
 * A method with this annotation, for each specified parameter, may only be passed arguments where
 * the class's {@code toString} method returns a {@code Det} or {@code PolyDet} result. When written
 * with no arguments, every parameter must obey this restriction. When one or more integers are
 * specified, only the parameters at those indices are checked.
 *
 * <p>For example, {@code @RequiresDetToString} on its own indicates that every argument must have a
 * determnistic {@code toString} method. Writing {@code @RequiresDetToString({0, 2})} indicates that
 * only the first and third arguments must have deterministic {@code toString} methods.
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
    int[] value() default {};
}
