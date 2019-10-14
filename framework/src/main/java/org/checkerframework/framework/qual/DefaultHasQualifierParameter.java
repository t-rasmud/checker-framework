package org.checkerframework.framework.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * On a checker class, a value of true indicates that for this checker all classes should be treated
 * as if they had {@code HasQualifierParameter} for that checker on by default. For such checkers,
 * {@code HasQualifierParameter} can be disabled for a specific type with the use of {@code
 * NoQualifierParameter}.
 */
@Target(ElementType.TYPE)
@Documented
public @interface DefaultHasQualifierParameter {

    /**
     * Indicates whether HasQualifierParameter should be turned on for all classes by default for a
     * given checker.
     */
    boolean value();
}
