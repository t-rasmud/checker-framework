package org.checkerframework.checker.test.junit;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class IterationTest extends CheckerFrameworkPerDirectoryTest {

    /**
     * Create a HasNextTest.
     *
     * @param testFiles the files containing test code, which will be type-checked
     */
    public IterationTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.iteration.IterationChecker.class,
                "iteration",
                "-Anomsgtext");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"iteration", "all-systems"};
    }
}
