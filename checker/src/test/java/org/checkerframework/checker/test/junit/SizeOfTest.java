// package test.java.org.checkerframework.checker.test.junit;
//
// import java.io.File;
// import java.util.List;
// import org.checkerframework.framework.test.CheckerFrameworkPerDirectoryTest;
// import org.junit.runners.Parameterized.Parameters;
//
// public class SizeOfTest extends CheckerFrameworkPerDirectoryTest {
//    /**
//     * Create a SizeOfTest.
//     *
//     * @param testFiles the files containing test code, which will be type-checked
//     */
//    public SizeOfTest(List<File> testFiles) {
//        super(
//                testFiles,
//                org.checkerframework.checker.sizeof.SizeOfChecker.class,
//                "sizeof",
//                "-Anomsgtext");
//    }
//
//    @Parameters
//    public static String[] getTestDirs() {
//        return new String[] {"sizeof", "all-systems"};
//    }
// }
