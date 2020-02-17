import org.checkerframework.checker.determinism.qual.Det;
import org.checkerframework.checker.determinism.qual.NonDet;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class PolyFieldAccess {
    static @Det Object staticDetField = new Object();
    public @PolyDet Object polyField = new Object();

    void nonDetAccess(@NonDet PolyFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.polyField;
        @NonDet Object o2 = this.polyField;
        // :: error: (assignment.type.incompatible)
        @PolyDet Object o3 = this.polyField;
    }

    void polyAccess(@PolyDet PolyFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.polyField;
        @NonDet Object o2 = this.polyField;
        @PolyDet Object o3 = this.polyField;
    }

    void detAccess(@Det PolyFieldAccess this) {
        @Det Object o = this.polyField;
    }

    @Det Object detMethod(
            @Det PolyFieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        @Det Object o = this.polyField;
        staticDetField = detObj;
        switch (x) {
            case 0:
                // :: error: (assignment.type.incompatible)
                polyField = nonDetObj;
                break;
            case 1:
                polyField = detObj;
                break;
            case 2:
                // :: error: (assignment.type.incompatible)
                polyField = polyDet;
                break;
            default:
        }

        return polyField;
    }

    @PolyDet Object polyMethod(
            @PolyDet PolyFieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        staticDetField = detObj;
        switch (x) {
            case 0:
                // :: error: (assignment.type.incompatible)
                polyField = nonDetObj;
                break;
            case 1:
                polyField = detObj;
                break;
            case 2:
                polyField = polyDet;
                break;
            default:
        }
        return polyField;
    }

    @NonDet Object nonDetMethod(
            @NonDet PolyFieldAccess this,
            @NonDet Object nonDetObj,
            @Det Object detObj,
            @PolyDet Object polyDet,
            @Det int x) {
        staticDetField = detObj;
        switch (x) {
            case 0:
                polyField = nonDetObj;
                break;
            case 1:
                polyField = detObj;
                break;
            case 2:
                polyField = polyDet;
                break;
            default:
        }
        return polyField;
    }

    @Det PolyFieldAccess PolyFieldAccess;

    void method(@NonDet PolyFieldAccess this) {
        // :: error: (assignment.type.incompatible)
        @Det Object o = this.PolyFieldAccess.polyField;
        // :: error: (invalid.field.assignment)
        this.PolyFieldAccess.polyField = new Object();
    }
}
