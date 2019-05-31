package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.InheritedAnnotation;

/**
 * Writing this annotation on a method declaration will ensure that if it has any parameter of type
 * Object, then the declared type of the corresponding argument must override {@code toString} and
 * its return type must be {@code @Det}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@InheritedAnnotation
@Target({ElementType.METHOD})
public @interface RequiresDetToString {}
