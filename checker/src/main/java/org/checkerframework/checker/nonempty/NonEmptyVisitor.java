package org.checkerframework.checker.nonempty;
import com.sun.source.tree.Tree;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.TypesUtils;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;


public class NonEmptyVisitor extends BaseTypeVisitor<NonEmptyAnnotatedTypeFactory> {
    public NonEmptyVisitor(BaseTypeChecker checker) {
        super(checker);
    }

    @Override
    public boolean isValidUse(AnnotatedTypeMirror.AnnotatedPrimitiveType type, Tree tree) {
        reportErrorOnNonCollections(type, tree);
        return super.isValidUse(type, tree);
    }

    @Override
    public boolean isValidUse(AnnotatedTypeMirror.AnnotatedArrayType type, Tree tree) {
        reportErrorOnNonCollections(type, tree);
        return super.isValidUse(type, tree);
    }

    @Override
    public boolean isValidUse(AnnotatedTypeMirror.AnnotatedDeclaredType declarationType,
                              AnnotatedTypeMirror.AnnotatedDeclaredType useType, Tree tree) {
        TypeMirror tm = declarationType.getUnderlyingType();
        TypeMirror collectionType = types.erasure(TypesUtils.typeFromClass(Collection.class, types, elements));
        TypeMirror iteratorType = types.erasure(TypesUtils.typeFromClass(Iterator.class, types, elements));
        TypeMirror mapType = types.erasure(TypesUtils.typeFromClass(Map.class, types, elements));
        boolean isCollection = types.isSubtype(tm, collectionType);
        boolean isIterator = types.isSubtype(tm, iteratorType);
        boolean isMap = types.isSubtype(tm, mapType);
        if (!isCollection && !isIterator && !isMap) {
            reportErrorOnNonCollections(useType, tree);
        }

        return super.isValidUse(declarationType, useType, tree);
    }

    private void reportErrorOnNonCollections(AnnotatedTypeMirror type, Tree tree) {
        if (type.hasAnnotation(atypeFactory.NONEMPTY)) {
            checker.reportError(tree, "invalid.nonempty");
        }
    }
}
