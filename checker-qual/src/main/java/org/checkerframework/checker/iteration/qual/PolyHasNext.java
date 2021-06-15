package org.checkerframework.checker.iteration.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.PolymorphicQualifier;

/**
 * A polymorphic qualifier for the {@code @HasNext} type system.
 *
 * <p>Any method written using {@code @PolyHasNext} conceptually has two versions: one in which
 * every instance of {@code @PolyHasNext} has been replaced by {@code @}{@link UnknownHasNext} and
 * one in which every instance of {@code @PolyHasNext} has been replaced by {@code @}{@link
 * HasNext}, for every Iterator argument.
 *
 * @checker_framework.manual #nonempty-checker NonEmpty Checker
 * @checker_framework.manual #qualifier-polymorphism Qualifier polymorphism
 */
@Documented
@PolymorphicQualifier(UnknownHasNext.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
public @interface PolyHasNext {}
