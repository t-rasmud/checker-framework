package org.checkerframework.checker.hasnext.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.SubtypeOf;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownHasNext.class})
public @interface HasNextFalse {}
