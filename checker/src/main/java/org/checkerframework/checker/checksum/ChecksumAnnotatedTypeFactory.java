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

/** Annotated type factory for the Checksum type system. */
public class ChecksumAnnotatedTypeFactory extends BaseAnnotatedTypeFactory {

    /** The @{@link ChecksummedBy} annotation. */
    protected final AnnotationMirror CHECKSUMMEDBY =
            AnnotationBuilder.fromClass(elements, ChecksummedBy.class);
    /** The @{@link NotChecksummed} annotation. */
    protected final AnnotationMirror NOT_CHECKSUMMED =
            AnnotationBuilder.fromClass(elements, NotChecksummed.class);

    /**
     * Create a new ChecksumAnnotatedTypeFactory.
     *
     * @param checker BaseTypeChecker
     */
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

    /** The Checksum qualifier hierarchy. */
    @Override
    public QualifierHierarchy createQualifierHierarchy(
            MultiGraphQualifierHierarchy.MultiGraphFactory factory) {
        return new ChecksumQualifierHierarchy(factory);
    }

    /** Checksum qualifier hierarchy. */
    class ChecksumQualifierHierarchy extends MultiGraphQualifierHierarchy {

        /**
         * ChecksumQualifierHierarchy constructor
         *
         * @param f MultiGraphFactory
         */
        public ChecksumQualifierHierarchy(MultiGraphFactory f) {
            super(f);
        }

        /**
         * Returns true if {@code am} is {@code @ChecksummedBy}.
         *
         * @param am AnnotationMirror
         * @return true if {@code am} is {@code @ChecksummedBy}
         */
        boolean isChecksummedBy(AnnotationMirror am) {
            return AnnotationUtils.areSameByName(am, CHECKSUMMEDBY);
        }

        /**
         * Defines subtyping relationship for {@link ChecksummedBy}. Two {@code @ChecksummedBy}
         * annotations are unrelated unless their values are the same.
         */
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
