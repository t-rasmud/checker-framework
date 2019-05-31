package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.InheritedAnnotation;

/**
 * A method with this annotation requires that all arguments corresponding to {@code Object}
 * parameters must return a {@code @Det String} when {@code toString} is called on them.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@InheritedAnnotation
@Target({ElementType.METHOD})
public @interface RequiresDetToString {}
