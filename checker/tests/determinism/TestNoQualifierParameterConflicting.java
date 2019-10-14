import java.util.*;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.HasQualifierParameter;
import org.checkerframework.framework.qual.NoQualifierParameter;

@HasQualifierParameter(NonDet.class)
@NoQualifierParameter(NonDet.class)
// :: error: (conflicting.qual.param)
public class TestNoQualifierParameterConflicting {}
