package org.checkerframework.checker.nonempty.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.DefaultQualifierInHierarchy;
import org.checkerframework.framework.qual.InvisibleQualifier;
import org.checkerframework.framework.qual.LiteralKind;
import org.checkerframework.framework.qual.QualifierForLiterals;
import org.checkerframework.framework.qual.SubtypeOf;
import org.checkerframework.framework.qual.TypeUseLocation;

/**
 * Indicates that the collection, map, or iterator is not known to be non-empty.
 *
 * <p>{@code @UnknownNonEmpty} is the top type qualifier in the {@link NonEmpty} hierarchy. It is
 * the default type qualifier, so programmers rarely need to write it.
 *
 * @checker_framework.manual #nonempty-checker NonEmpty Checker
 */
@InvisibleQualifier
@SubtypeOf({})
@DefaultQualifierInHierarchy
@DefaultFor({TypeUseLocation.LOWER_BOUND})
@QualifierForLiterals({LiteralKind.NULL})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface UnknownNonEmpty {}
