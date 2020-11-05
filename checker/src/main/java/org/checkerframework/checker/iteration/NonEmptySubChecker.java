package org.checkerframework.checker.iteration;

import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.qual.RelevantJavaTypes;

import java.util.Collection;

@RelevantJavaTypes(Collection.class)
public class NonEmptySubChecker extends BaseTypeChecker { }
