package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Type declaration annotation that indicates that the type is a collection of objects. May be
 * written on classes that are not ordinarily thought of as collections, such as Iterators. The
 * objects may or may not be in a deterministic order. java.util.Iterator are collections.
 *
 * <p>Any class or interface that extends or implements a collection type is automatically
 * considered a collection type even if this annotation isn't written on the class or interface.
 *
 * @checker_framework.manual #determinism-checker Determinism Checker
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CollectionType {}
