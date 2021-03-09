package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.InheritedAnnotation;

/**
 * The receiver of a method with this annotation must satisfy the following property: if the
 * receiver qualifier type is {@code @NonDet}, then its type argument must be {@code @NonDet}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@InheritedAnnotation
@Target({ElementType.METHOD})
public @interface CheckReceiverForMutation {}
