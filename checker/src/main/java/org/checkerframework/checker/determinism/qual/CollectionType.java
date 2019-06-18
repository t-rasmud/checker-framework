package org.checkerframework.checker.determinism.qual;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;

/**
 * Type declaration annotation that indicates that the type is a collection of objects. The objects
 * may or may not be in a deterministic order.
 */
@Target(ElementType.TYPE)
@Inherited
public @interface CollectionType {}
