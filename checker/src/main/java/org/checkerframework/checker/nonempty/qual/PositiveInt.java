package org.checkerframework.checker.nonempty.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.*;

/**
 * Indicates that the integer has value greater than 0.
 *
 * @checker_framework.manual #nonempty-checker NonEmpty Checker
 */
@SubtypeOf(UnknownInt.class)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@TargetLocations({TypeUseLocation.EXPLICIT_LOWER_BOUND, TypeUseLocation.EXPLICIT_UPPER_BOUND})
@RelevantJavaTypes(int.class)
public @interface PositiveInt {}
