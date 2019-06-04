package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.InheritedAnnotation;

/**
 * A Method with this annotation expects a {@code @Det} result when calling {@code toString} on
 * {@code Object} or {@code Object[]} arguments.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@InheritedAnnotation
@Target({ElementType.METHOD})
public @interface RequiresDetToString {}
