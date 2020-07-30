package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class DeterminismDetDefaultTest extends CheckerFrameworkPerDirectoryTest {

    public DeterminismDetDefaultTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.determinism.DeterminismChecker.class,
                "determinism-det-default",
                "-Anomsgtext",
                "-Alint=enableconditionaltypecheck,useDetDefault");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"determinism-det-default"};
    }
}
