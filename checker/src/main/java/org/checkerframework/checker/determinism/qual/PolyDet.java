package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.framework.qual.DefaultFor;
import org.checkerframework.framework.qual.PolymorphicQualifier;
import org.checkerframework.framework.qual.TypeUseLocation;

/**
 * A polymorphic qualifier for the determinism type system.
 *
 * <p>Any method written using {@code @PolyDet} conceptually has three versions: one in which every
 * instance of {@code @PolyDet} has been replaced by {@link NonDet}, one in which every instance of
 * {@code @PolyDet} has been replaced by {@link OrderNonDet}, and one in which every instance of
 * {@code @PolyDet} has been replaced by {@link Det}.
 *
 * <p>Optionally, {@code @PolyDet} takes one of the three String values "up", "down" or "use". If
 * {@code @PolyDet} resolves to {@link OrderNonDet}, {@code @PolyDet("up")} gets replaced by {@link
 * NonDet}, and {@code @PolyDet("down")} by {@link Det}. Since the type {@link OrderNonDet} is
 * invalid for non-collections, any return type that isn't a collection should be annotated either
 * as {@code @PolyDet("up") or @PolyDet("down")}. {@code @PolyDet("use")} should be the annotation
 * on method parameters that you do not want instantiated. For example, a method that is annotated
 * as {@code void method_name (@PolyDet a, @PolyDet("use") b)} would not be instantiated as {@code
 * void method_name (@Det a, @NonDet b)}. This is especially useful in preventing methods to
 * non-deterministically modify the state of a deterministic receiver.
 *
 * @checker_framework.manual #determinism-checker Nullness Checker
 * @checker_framework.manual #qualifier-polymorphism Qualifier polymorphism
 */
@Documented
@PolymorphicQualifier(NonDet.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@DefaultFor({TypeUseLocation.PARAMETER, TypeUseLocation.RETURN, TypeUseLocation.RECEIVER})
public @interface PolyDet {
    String value() default "";
}
