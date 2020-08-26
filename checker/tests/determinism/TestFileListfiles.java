import java.io.File;
import org.checkerframework.checker.determinism.qual.PolyDet;

public class TestFileListfiles {
    void test(@PolyDet File node) {
        @PolyDet File @PolyDet("upDet") [] result = node.listFiles();
    }
}
