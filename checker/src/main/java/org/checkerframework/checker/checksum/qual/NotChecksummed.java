package org.checkerframework.checker.checksum.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeUseLocation;

/**
 * This qualifier represents values that are not checksummed. This is the default type; programmers
 * do not need to write it.
 *
 * @checker_framework.manual #checksum-checker Checksum Checker
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@SubtypeOf({UnknownChecksum.class})
@DefaultQualifierInHierarchy
@DefaultFor({TypeUseLocation.UPPER_BOUND, TypeUseLocation.EXCEPTION_PARAMETER})
public @interface NotChecksummed {}
