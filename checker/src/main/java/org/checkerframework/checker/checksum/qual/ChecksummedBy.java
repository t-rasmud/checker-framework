package org.checkerframework.checker.checksum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * This qualifier represents values that are expected to be checksummed by the checksum expression
 * as in {@code @ChecksummedBy("checksumexpr")}. This is unrelated to {@code @NotChecksummed} in the
 * type hierarchy, and {@code @ChecksummedBy("a")} is unrelated to {@code @ChecksummedBy("b")}.
 *
 * @checker_framework.manual #checksum-checker Checksum Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownChecksum.class})
public @interface ChecksummedBy {
    /* Expression that checksums this value. */
    String value() default "";
}
