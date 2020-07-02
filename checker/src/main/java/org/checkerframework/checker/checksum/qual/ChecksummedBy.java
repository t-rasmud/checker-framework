package org.checkerframework.checker.checksum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * A type having this qualifier represents values that are checksummed by the checksum expression as
 * in {@code @ChecksummedBy("checksumexpr")}. This is unrelated to {@code @NotChecksummed} in the
 * type hierarchy, and {@code @ChecksummedBy("a")} is unrelated to {@code @ChecksummedBy("b")}.
 *
 * @checker_framework.manual #checksum-checker Checksum Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownChecksum.class})
public @interface ChecksummedBy {
    /**
     * If an expression D has type {@code @ChecksummedBy("CS")}, then the expression CS checksums
     * the value of expression D.
     *
     * @return Java expression
     */
    String value() default "";
}
