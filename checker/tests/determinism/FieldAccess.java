import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class FieldAccess {
    static @Det Object staticDetField = new Object();
    public @Det Object detField = new Object();

    void nonDetAccess(@NonDet FieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.detField;
        @NonDet Object o2 = this.detField;
        // :: error: (assignment.type.incompatible)
        @PolyDet Object o3 = this.detField;
    }

    void polyAccess(@PolyDet FieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.detField;
        @NonDet Object o2 = this.detField;
        @PolyDet Object o3 = this.detField;
    }

    void detAccess(@Det FieldAccess this) {
        @Det Object o = this.detField;
    }

    @Det Object detMethod(
            @Det FieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        @Det Object o = this.detField;
        staticDetField = detObj;
        switch (x) {
            case 0:
                // :: error: (assignment.type.incompatible)
                detField = nonDetObj;
                break;
            case 1:
                detField = detObj;
                break;
            case 2:
                // :: error: (assignment.type.incompatible)
                detField = polyDet;
                break;
            default:
        }

        return detField;
    }

    @PolyDet Object polyMethod(
            @PolyDet FieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        staticDetField = detObj;
        switch (x) {
            case 0:
                // :: error: (invalid.field.assignment)
                detField = nonDetObj;
                break;
            case 1:
                // :: error: (invalid.field.assignment)
                detField = detObj;
                break;
            case 2:
                // :: error: (invalid.field.assignment)
                detField = polyDet;
                break;
            default:
        }
        return detField;
    }

    @NonDet Object nonDetMethod(
            @NonDet FieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        staticDetField = detObj;
        switch (x) {
            case 0:
                // :: error: (invalid.field.assignment)
                detField = nonDetObj;
                break;
            case 1:
                // :: error: (invalid.field.assignment)
                detField = detObj;
                break;
            case 2:
                // :: error: (invalid.field.assignment)
                detField = polyDet;
                break;
            default:
        }
        return detField;
    }

    @Det FieldAccess fieldAccess;

    void method(@NonDet FieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.fieldAccess.detField;
        // :: error: (invalid.field.assignment)
        this.fieldAccess.detField = new Object();
    }
}
