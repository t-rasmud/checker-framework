package org.checkerframework.framework.qual;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Documented
public @interface NoQualifierParameter {

    /**
     * Class of the top qualifier for the hierarchy for which this class has no qualifier parameter.
     */
    Class<? extends Annotation>[] value();
}
