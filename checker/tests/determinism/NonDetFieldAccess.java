import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class NonDetFieldAccess {
    static @Det Object staticDetField = new Object();
    public @NonDet Object nonDetField = new Object();

    void nonDetAccess(@NonDet NonDetFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.nonDetField;
        @NonDet Object o2 = this.nonDetField;
        // :: error: (assignment.type.incompatible)
        @PolyDet Object o3 = this.nonDetField;
    }

    void polyAccess(@PolyDet NonDetFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.nonDetField;
        @NonDet Object o2 = this.nonDetField;
        // :: error: (assignment.type.incompatible)
        @PolyDet Object o3 = this.nonDetField;
    }

    void detAccess(@Det NonDetFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.nonDetField;
    }

    @Det Object detMethod(
            @Det NonDetFieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.nonDetField;
        staticDetField = detObj;
        switch (x) {
            case 0:
                nonDetField = nonDetObj;
                break;
            case 1:
                nonDetField = detObj;
                break;
            case 2:
                nonDetField = polyDet;
                break;
            default:
        }
        // :: error: (return.type.incompatible)
        return nonDetField;
    }

    @PolyDet Object polyMethod(
            @PolyDet NonDetFieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        staticDetField = detObj;
        switch (x) {
            case 0:
                nonDetField = nonDetObj;
                break;
            case 1:
                nonDetField = detObj;
                break;
            case 2:
                nonDetField = polyDet;
                break;
            default:
        }
        // :: error: (return.type.incompatible)
        return nonDetField;
    }

    @NonDet Object nonDetMethod(
            @NonDet NonDetFieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        staticDetField = detObj;
        switch (x) {
            case 0:
                nonDetField = nonDetObj;
                break;
            case 1:
                nonDetField = detObj;
                break;
            case 2:
                nonDetField = polyDet;
                break;
            default:
        }
        return nonDetField;
    }

    @Det NonDetFieldAccess fieldAccess;

    void method(@NonDet NonDetFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.fieldAccess.nonDetField;
        this.fieldAccess.nonDetField = new Object();
    }
}
