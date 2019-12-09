package tests;

import java.io.File;
import java.util.List;
import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
import org.junit.runners.Parameterized.Parameters;

public class TaintingDefaultHasQualifierParameterTest extends CheckerFrameworkPerDirectoryTest {

    public TaintingDefaultHasQualifierParameterTest(List<File> testFiles) {
        super(
                testFiles,
                org.checkerframework.checker.tainting.TaintingChecker.class,
                "tainting-defaulthasqualifierparameter",
                "-Anomsgtext",
                "-AdefaultHasQualifierParameter=org.checkerframework.checker.tainting.qual.Tainted:^(package1|package2)\\..*");
    }

    @Parameters
    public static String[] getTestDirs() {
        return new String[] {"tainting-defaulthasqualifierparameter", "all-systems"};
    }
}