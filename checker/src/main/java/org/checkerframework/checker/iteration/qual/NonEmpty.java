package org.checkerframework.checker.iteration.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

import org.checkerframework.framework.qual.RelevantJavaTypes;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * Indicates that the collection value assigned to the annotated variable is non-empty.
 *
 * @see EnsuresNonEmpty
 * @see EnsuresNonEmptyIf
 * @checker_framework.manual #map-key-checker Map Key Checker
 */
@SubtypeOf(UnknownNonEmpty.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface NonEmpty {}
