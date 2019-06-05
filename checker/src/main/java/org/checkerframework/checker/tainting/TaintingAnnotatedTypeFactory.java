package org.checkerframework.checker.tainting;

import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.Tree;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.AnnotatedTypeMirror;
import org.checkerframework.framework.type.typeannotator.ImplicitsTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.ListTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.PropagationTypeAnnotator;
import org.checkerframework.framework.type.typeannotator.TypeAnnotator;
import org.checkerframework.javacutil.TreeUtils;

/** {@link org.checkerframework.framework.type.AnnotatedTypeFactory} for the Tainting Checker. */
public class TaintingAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    //    private final AnnotationMirror UNTAINTED =
    //            AnnotationBuilder.fromClass(elements, Untainted.class);

    /** Creates a tainting annotated type factory. */
    public TaintingAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        postInit();
    }

    @Override
    protected void addComputedTypeAnnotations(
            Tree tree, AnnotatedTypeMirror type, boolean iUseFlow) {
        super.addComputedTypeAnnotations(tree, type, iUseFlow);
        if (TreeUtils.isArrayLengthAccess(tree)) {
            AnnotatedTypeMirror arrayType =
                    getAnnotatedType(((MemberSelectTree) tree).getExpression());
            type.replaceAnnotations(arrayType.getEffectiveAnnotations());
        }
    }

    @Override
    protected TypeAnnotator createTypeAnnotator() {
        List<TypeAnnotator> typeAnnotators = new ArrayList<>();

        typeAnnotators.add(new PropagationTypeAnnotator(this));
        ImplicitsTypeAnnotator implicitsTypeAnnotator = new ImplicitsTypeAnnotator(this);
        typeAnnotators.add(implicitsTypeAnnotator);
        //        implicitsTypeAnnotator.addTypeClass(AnnotatedArrayType.class, UNTAINTED);
        return new ListTypeAnnotator(typeAnnotators);
    }
}
