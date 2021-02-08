// package org.checkerframework.checker.sizeof.qual;
//
// import java.lang.annotation.Documented;
// import java.lang.annotation.ElementType;
// import java.lang.annotation.Retention;
// import java.lang.annotation.RetentionPolicy;
// import java.lang.annotation.Target;
// import org.checkerframework.framework.qual.JavaExpression;
// import org.checkerframework.framework.qual.RelevantJavaTypes;
// import org.checkerframework.framework.qual.SubtypeOf;
//
// @Documented
// @Retention(RetentionPolicy.RUNTIME)
// @Target({ElementType.TYPE_USE, ElementType.TYPE_PARAMETER})
// @SubtypeOf(UnknownSizeOf.class)
// @RelevantJavaTypes(int.class)
// public @interface SizeOf {
//    @JavaExpression
//    public String[] value();
// }
