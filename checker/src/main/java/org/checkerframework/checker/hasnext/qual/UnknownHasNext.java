package org.checkerframework.checker.hasnext.qual;

import java.lang.annotation.*;
import org.checkerframework.framework.qual.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({})
@DefaultQualifierInHierarchy
@DefaultFor(value = TypeUseLocation.LOWER_BOUND, types = Void.class)
@QualifierForLiterals(LiteralKind.NULL)
public @interface UnknownHasNext {}
