package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class DeterminismDefaultHasQualifierParameterTest extends CheckerFrameworkPerDirectoryTest {

    public DeterminismDefaultHasQualifierParameterTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.determinism.DeterminismChecker.class,
                "determinism-defaulthasqualifierparameter",
                "-Anomsgtext",
                "-Alint=enableconditionaltypecheck",
                "-AdefaultHasQualifierParameter=org.checkerframework.checker.determinism.qual.NonDet:(package1|package2).*");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"determinism-defaulthasqualifierparameter", "all-systems"};
    }
}
