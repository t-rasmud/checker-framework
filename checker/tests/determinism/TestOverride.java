import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class TestOverride {
    protected @PolyDet int mult(@PolyDet TestOverride this, @PolyDet int a) {
        return a * a;
    }

    protected @PolyDet int mult1(@PolyDet int a) {
        return a * a;
    }

    protected @PolyDet ArrayList<Integer> newList(@PolyDet int a) {
        return new ArrayList<Integer>(a);
    }

    protected @PolyDet("up") int getList(@PolyDet ArrayList<Integer> a) {
        return a.get(0);
    }

    protected @NonDet int getList1(@Det ArrayList<Integer> a, @NonDet int b) {
        // :: error: (method.invocation.invalid)
        return a.get(b);
    }
}

class Child extends TestOverride {
    @Override
    protected @Det int mult(@PolyDet int a) {
        return 5;
    }

    @Override
    // :: error: (override.return.invalid)
    protected @Det ArrayList<@Det Integer> newList(@NonDet int a) {
        // :: warning: (cast.unsafe.constructor.invocation)
        return new @Det ArrayList<Integer>(a);
    }

    @Override
    // :: error: (override.param.invalid)
    protected @PolyDet int getList(@Det ArrayList<Integer> a) {
        return a.get(0);
    }

    @Override
    protected @Det int mult1(@NonDet Child this, @NonDet int a) {
        return 5;
    }
}

class SecondChild extends TestOverride {
    @Override
    protected @Det int mult1(@NonDet SecondChild this, @NonDet int a) {
        // :: error: (return.type.incompatible)
        return a;
    }
}
