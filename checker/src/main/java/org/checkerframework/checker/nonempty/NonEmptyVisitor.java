package org.checkerframework.checker.nonempty;

import com.sun.source.tree.Tree;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import org.checkerframework.checker.nonempty.qual.NonEmpty;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.common.basetype.BaseTypeVisitor;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.TreeUtils;
import org.checkerframework.javacutil.TypesUtils;

/** Visitor for the NonEmpty Checker. */
public class NonEmptyVisitor extends BaseTypeVisitor<NonEmptyAnnotatedTypeFactory> {
    /** The java.util.Collection interface */
    private final TypeMirror collectionType =
            types.erasure(TypesUtils.typeFromClass(Collection.class, types, elements));
    /** The java.util.Iterator interface */
    private final TypeMirror iteratorType =
            types.erasure(TypesUtils.typeFromClass(Iterator.class, types, elements));
    /** The java.util.Map interface */
    private final TypeMirror mapType =
            types.erasure(TypesUtils.typeFromClass(Map.class, types, elements));
    /** The processing environment */
    ProcessingEnvironment processingEnv = atypeFactory.getProcessingEnv();
    /** The {@code Collection.size()} method */
    ExecutableElement getMethod = TreeUtils.getMethod("java.util.List", "get", 1, processingEnv);
    /** The {@literal @}{@link NonEmpty} annotation. */
    private final AnnotationMirror NONEMPTY = AnnotationBuilder.fromClass(elements, NonEmpty.class);

    /**
     * NonEmptyVisitor constructor.
     *
     * @param checker BaseTypeChecker
     */
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
        reportErrorOnNonEmptyType(type, tree);
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
        if (TreeUtils.isClassLiteral(tree)) {
            // Don't validate class literals
            return true;
        }
        TypeMirror tm = declarationType.getUnderlyingType();
        boolean isCollection = types.isSubtype(tm, collectionType);
        boolean isIterator = types.isSubtype(tm, iteratorType);
        boolean isMap = types.isSubtype(tm, mapType);
        if (!isCollection && !isIterator && !isMap) {
            reportErrorOnNonEmptyType(useType, tree);
        }

        return super.isValidUse(declarationType, useType, tree);
    }

    /**
     * Reports an error if {@code type} has the qualifier {@code @NonEmpty}.
     *
     * @param type AnnotatedTypeMirror
     * @param tree Tree
     */
    private void reportErrorOnNonEmptyType(AnnotatedTypeMirror type, Tree tree) {
        if (type.hasAnnotation(NONEMPTY)) {
            checker.reportError(tree, "invalid.nonempty");
        }
    }

    //    @Override
    //    protected void reportMethodInvocabilityError(
    //            MethodInvocationTree node, AnnotatedTypeMirror found, AnnotatedTypeMirror
    // expected) {
    //        if (TreeUtils.isMethodInvocation(node, getMethod, processingEnv)) {
    //            ExpressionTree indexArg = node.getArguments().get(0);
    //            GenericAnnotatedTypeFactory<?, ?, ?, ?> sizeOfATF =
    //                    atypeFactory.getTypeFactoryOfSubchecker(SizeOfChecker.class);
    //            AnnotatedTypeMirror indexArgAnnoMirror = sizeOfATF.getAnnotatedType(indexArg);
    //            AnnotationMirror indexArgAnno = indexArgAnnoMirror.getAnnotation();
    //            if (AnnotationUtils.areSameByClass(indexArgAnno, SizeOf.class)) {
    //                List<String> elementValues =
    //
    // ValueCheckerUtils.getValueOfAnnotationWithStringArgument(indexArgAnno);
    //                String methodSelect = node.getMethodSelect().toString();
    //                String[] splitMethodSelect = methodSelect.split("\\.");
    //                if (elementValues.size() == 1
    //                        && elementValues.get(0).equals(splitMethodSelect[0])) {
    //                    return;
    //                }
    //            }
    //        }
    //        super.reportMethodInvocabilityError(node, found, expected);
    //    }
}
