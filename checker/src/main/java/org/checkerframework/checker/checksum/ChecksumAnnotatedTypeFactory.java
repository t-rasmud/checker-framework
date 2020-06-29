package org.checkerframework.checker.checksum;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import org.checkerframework.checker.checksum.qual.*;
import org.checkerframework.common.basetype.BaseAnnotatedTypeFactory;
import org.checkerframework.common.basetype.BaseTypeChecker;
import org.checkerframework.framework.type.QualifierHierarchy;
import org.checkerframework.framework.util.MultiGraphQualifierHierarchy;
import org.checkerframework.javacutil.AnnotationBuilder;
import org.checkerframework.javacutil.AnnotationUtils;

public class ChecksumAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {
    protected final AnnotationMirror UNKNOWN_CHECKSUM =
            AnnotationBuilder.fromClass(elements, UnknownChecksum.class);
    protected final AnnotationMirror CHECKSUMMEDBY =
            AnnotationBuilder.fromClass(elements, ChecksummedBy.class);
    protected final AnnotationMirror NOT_CHECKSUMMED =
            AnnotationBuilder.fromClass(elements, NotChecksummed.class);

    public ChecksumAnnotatedTypeFactory(BaseTypeChecker checker) {
        super(checker);
        this.postInit();
    }

    @Override
    protected Set<Class<? extends Annotation>> createSupportedTypeQualifiers() {
        return new LinkedHashSet<>(
                Arrays.asList(
                        UnknownChecksum.class,
                        NotChecksummed.class,
                        ChecksummedBy.class,
                        ChecksumBottom.class,
                        PolyChecksum.class));
    }

    @Override
    public QualifierHierarchy createQualifierHierarchy(
            MultiGraphQualifierHierarchy.MultiGraphFactory factory) {
        return new ChecksumQualifierHierarchy(factory);
    }

    class ChecksumQualifierHierarchy extends MultiGraphQualifierHierarchy {

        public ChecksumQualifierHierarchy(MultiGraphFactory f) {
            super(f);
        }

        boolean isChecksummedBy(AnnotationMirror am) {
            return AnnotationUtils.areSameByName(am, CHECKSUMMEDBY);
        }

        @Override
        public boolean isSubtype(AnnotationMirror subAnno, AnnotationMirror superAnno) {
            boolean isLhsChecksummedBy = isChecksummedBy(superAnno);
            boolean isRhsChecksummedBy = isChecksummedBy(subAnno);

            if (isLhsChecksummedBy && isRhsChecksummedBy) {
                String lhsValue =
                        AnnotationUtils.getElementValue(superAnno, "value", String.class, true);
                String rhsValue =
                        AnnotationUtils.getElementValue(subAnno, "value", String.class, true);

                return lhsValue.equals(rhsValue);
            }

            if (isLhsChecksummedBy) {
                superAnno = CHECKSUMMEDBY;
            } else if (isRhsChecksummedBy) {
                subAnno = CHECKSUMMEDBY;
            }

            return super.isSubtype(subAnno, superAnno);
        }
    }
}
