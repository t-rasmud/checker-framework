import java.util.*;
import org.checkerframework.checker.determinism.qual.*;
import org.checkerframework.framework.qual.HasQualifierParameter;

@HasQualifierParameter(NonDet.class)
public class TreeSetIssue {
    public @PolyDet Set<@PolyDet Integer> ints = new @PolyDet TreeSet<@PolyDet Integer>();
}
