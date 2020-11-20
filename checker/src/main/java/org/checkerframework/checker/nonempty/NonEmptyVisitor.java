package org.checkerframework.checker.nonempty;

import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TypesUtils;

/** Visitor for the NonEmpty Checker. */
public class NonEmptyVisitor extends BaseTypeVisitor<BaseAnnotatedTypeFactory> {
    private final TypeMirror collectionType =
            types.erasure(TypesUtils.typeFromClass(Collection.class, types, elements));
    private final TypeMirror iteratorType =
            types.erasure(TypesUtils.typeFromClass(Iterator.class, types, elements));
    private final TypeMirror mapType =
            types.erasure(TypesUtils.typeFromClass(Map.class, types, elements));
    /** The {@literal @}{@link NonEmpty} annotation. */
    private final AnnotationMirror NONEMPTY = AnnotationBuilder.fromClass(elements, NonEmpty.class);

    public NonEmptyVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    /**
     * Reports error if {@code @NonEmpty} is written on primitive types.
     *
     * @param type the use of the primitive type
     * @param tree the tree where the type is used
     * @return boolean
     */
    @Override
    public boolean isValidUse(AnnotatedTypeMirror.AnnotatedPrimitiveType type, Tree tree) {
        reportErrorOnNonCollections(type, tree);
        return super.isValidUse(type, tree);
    }

    /**
     * Reports error if {@code @NonEmpty} is written on array types.
     *
     * @param type the array type use
     * @param tree the tree where the type is used
     * @return boolean
     */
    @Override
    public boolean isValidUse(AnnotatedTypeMirror.AnnotatedArrayType type, Tree tree) {
        reportErrorOnNonCollections(type, tree);
        return super.isValidUse(type, tree);
    }

    /**
     * Reports error if {@code @NonEmpty} is written on declared types other than Collection,
     * Iterator, Map, or their subtypes.
     *
     * @param declarationType the type of the class (TypeElement)
     * @param useType the use of the class (instance type)
     * @param tree the tree where the type is used
     * @return boolean
     */
    @Override
    public boolean isValidUse(
            AnnotatedTypeMirror.AnnotatedDeclaredType declarationType,
            AnnotatedTypeMirror.AnnotatedDeclaredType useType,
            Tree tree) {
        TypeMirror tm = declarationType.getUnderlyingType();
        boolean isCollection = types.isSubtype(tm, collectionType);
        boolean isIterator = types.isSubtype(tm, iteratorType);
        boolean isMap = types.isSubtype(tm, mapType);
        if (!isCollection && !isIterator && !isMap) {
            reportErrorOnNonCollections(useType, tree);
        }

        return super.isValidUse(declarationType, useType, tree);
    }

    private void reportErrorOnNonCollections(AnnotatedTypeMirror type, Tree tree) {
        if (type.hasAnnotation(NONEMPTY)) {
            checker.reportError(tree, "invalid.nonempty");
        }
    }
}
