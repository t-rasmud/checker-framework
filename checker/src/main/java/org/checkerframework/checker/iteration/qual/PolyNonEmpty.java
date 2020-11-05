package org.checkerframework.checker.iteration.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PolymorphicQualifier;

/**
 * A polymorphic qualifier for the Map Key (@NonEmpty) type system.
 *
 * <p>Any method written using {@code @PolyNonEmpty} conceptually has an arbitrary number of
 * versions: one in which every instance of {@code @PolyNonEmpty} has been replaced by
 * {@code @}{@link UnknownNonEmpty}, one in which every instance of {@code @}{@link PolyNonEmpty}
 * has been replaced by {@code @}{@link NonEmptyBottom}, and ones in which every instance of
 * {@code @PolyNonEmpty} has been replaced by {@code @}{@code NonEmpty}, for every possible
 * combination of map arguments.
 *
 * @checker_framework.manual #nullness-checker Nullness Checker
 * @checker_framework.manual #qualifier-polymorphism Qualifier polymorphism
 */
@Documented
@PolymorphicQualifier(UnknownNonEmpty.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface PolyNonEmpty {}
