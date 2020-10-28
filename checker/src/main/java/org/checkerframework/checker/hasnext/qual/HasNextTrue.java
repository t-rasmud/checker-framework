package org.checkerframework.checker.hasnext.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownHasNext.class})
/**
 * An expression whose type has this annotation is an iterator that has a next value -- that is,
 * {@code next()} will not throw NoSuchElementException.
 */
public @interface HasNextTrue {}
