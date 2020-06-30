package org.checkerframework.checker.checksum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.SubtypeOf;

/**
 * This is the bottom type qualifier. It is used only for the null value. Programmers should never
 * write it (except in rare cases in wildcard types).
 *
 * @checker_framework.manual #checksum-checker Checksum Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({NotChecksummed.class, ChecksummedBy.class})
public @interface ChecksumBottom {}
