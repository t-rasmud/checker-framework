import java.util.*;
import org.checkerframework.checker.determinism.qual.*;

public class ClassDeclarationTypeParam<T extends @NonDet Object> extends ArrayList<T> {}
