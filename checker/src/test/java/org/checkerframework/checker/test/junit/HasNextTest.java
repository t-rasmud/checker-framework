package org.checkerframework.checker.test.junit;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class HasNextTest extends CheckerFrameworkPerDirectoryTest {

    /**
     * Create a HasNextTest.
     *
     * @param testFiles the files containing test code, which will be type-checked
     */
    public HasNextTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.hasnext.HasNextChecker.class,
                "hasnext",
                "-Anomsgtext");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"hasnext", "all-systems"};
    }
}
