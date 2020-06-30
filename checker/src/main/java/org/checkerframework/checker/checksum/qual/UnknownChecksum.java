package org.checkerframework.checker.checksum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * A type having the qualifier {@code @UnknownChecksum} represents values that might or might not be
 * protected by checksums. A type annotated with this qualifier cannot be used where {@code @}{@link
 * NotChecksummed} is expected, and it cannot be used where {@code @}{@link ChecksummedBy} is
 * expected. Programmers should not write it. It generally indicates a programming error or
 * unnecessarily complex code where the meaning of a value depends on some external condition.
 *
 * <p>{@code @UnknownChecksum} is the top of the Checksum qualifier hierarchy.
 *
 * @checker_framework.manual #checksum-checker Checksum Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({})
public @interface UnknownChecksum {}
