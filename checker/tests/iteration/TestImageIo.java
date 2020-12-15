package iteration;

import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;

public class TestImageIo {
    void test() {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        iter.next();
    }
}
