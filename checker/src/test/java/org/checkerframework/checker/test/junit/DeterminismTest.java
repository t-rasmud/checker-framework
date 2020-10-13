package org.checkerframework.checker.test.junit;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class DeterminismTest extends CheckerFrameworkPerDirectoryTest {

    public DeterminismTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.determinism.DeterminismChecker.class,
                "determinism",
                "-Anomsgtext",
                "-Alint=enableconditionaltypecheck",
                "-AusePolyDefault");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"determinism", "all-systems"};
    }
}
