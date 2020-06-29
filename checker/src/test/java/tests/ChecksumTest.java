package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class ChecksumTest extends CheckerFrameworkPerDirectoryTest {

    /**
     * Create a ChecksumTest.
     *
     * @param testFiles the files containing test code, which will be type-checked
     */
    public ChecksumTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.checksum.ChecksumChecker.class,
                "checksum",
                "-Anomsgtext");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"checksum", "all-systems"};
    }
}
