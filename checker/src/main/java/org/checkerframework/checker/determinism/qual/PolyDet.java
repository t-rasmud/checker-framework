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
 * @checker_framework.manual #determinism-checker Determinism Checker
 * @checker_framework.manual #qualifier-polymorphism Qualifier polymorphism
 */
@Documented
@PolymorphicQualifier(NonDet.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
@DefaultFor({TypeUseLocation.PARAMETER, TypeUseLocation.RETURN, TypeUseLocation.RECEIVER})
public @interface PolyDet {
    /**
     * Optionally, {@code @PolyDet} takes one of the three String values "up", "down" or "use".
     *
     * <p>If {@code @PolyDet} resolves to {@link OrderNonDet}, {@code @PolyDet("up")} gets replaced
     * by {@link NonDet}, and {@code @PolyDet("down")} by {@link Det}.
     *
     * <p>TODO: The below comment is confusing. Here are two things that are confusing. First, even
     * with regular rules, the method would never be instantiated as {@code void method_name(@Det
     * a, @NonDet b)}. The only two instantiations would be {@code void method_name(@Det a, @Det b)}
     * and {@code void method_name(@NonDet a, @NonDet b)}. Second, regarding "you do not want
     * instantiated": every {@code @PolyDet} will be instatiated, the only question is what it is
     * instantiated as. Please rewrite the below text. Also, the "this is especially useful" would
     * be good text for the manual; move it there from here.
     *
     * <p>{@code @PolyDet("use")} should be the annotation on method parameters that you do not want
     * instantiated. For example, a method that is annotated as {@code void method_name (@PolyDet
     * a, @PolyDet("use") b)} would not be instantiated as {@code void method_name(@Det a, @NonDet
     * b)}. This is especially useful in preventing methods from non-deterministically modifying the
     * state of a deterministic receiver.
     */
    String value() default "";
}
