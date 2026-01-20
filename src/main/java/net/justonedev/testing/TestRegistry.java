package net.justonedev.testing;

import net.justonedev.response.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A.
 * @author uwwfh
 */
public class TestRegistry {

    private static final String ERROR_FOLDER_INVALID = "Error, the specified folder is not valid (does not exist or is not folder): %s";
    private static final String TESTCASE_CASE_DELIMITER = "%T";
    private static final String TESTCASE_LINE_DELIMITER = "%n";
    private static final String SPACE_ENCODING = "§";
    private static final String SPACE_DECODING = " ";

    private static final String TESTFILE_FILE_EXTENSION = ".protocol";
    private static final String TESTFILE_FILE_EXTENSION_ALT = ".txt";

    private final File testFolder;
    private final String testcases;

    private List<Test> tests;

    // todo remove for this time
    /**
     * A.
     * @param folder
     * @throws IllegalArgumentException A.
     */
    public TestRegistry(File folder) {
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException(ERROR_FOLDER_INVALID.formatted(folder.getAbsolutePath()));
        }
        this.testFolder = folder;
        this.testcases = null;
    }

    /**
     * A.
     * @param testcases
     * @throws IllegalArgumentException A.
     */
    public TestRegistry(String testcases) {
        this.testFolder = null;
        this.testcases = testcases.replace(System.lineSeparator(), "")
                .replace(SPACE_ENCODING, SPACE_DECODING);
    }

    // todo private
    /**
     * A.
     * @return A.
     */
    public List<Test> getTests() {
        if (!isLoaded()) {
            return List.of();
        }
        return new ArrayList<>(tests);
    }

    // todo private
    /**
     * A.
     * @return A.
     */
    public boolean isLoaded() {
        return tests != null;
    }

    /**
     * A.
     * @return A.
     */
    public List<Test> loadTests() {
        if (this.testFolder != null) {
            return loadTestsFromFile();
        }
        return loadTestsFromString();
    }

    private List<Test> loadTestsFromFile() {
        this.tests = new ArrayList<>();
        for (File file : Objects.requireNonNull(
                testFolder.listFiles((dir, name) -> name.endsWith(TESTFILE_FILE_EXTENSION)
                        || name.endsWith(TESTFILE_FILE_EXTENSION_ALT)))) {
            Optional<Test> importedTest = Test.importNew(file);
            importedTest.ifPresent((test) -> tests.add(test));
        }
        return getTests();
    }

    private List<Test> loadTestsFromString() {
        this.tests = new ArrayList<>();
        for (String testcase : this.testcases.split(TESTCASE_CASE_DELIMITER)) {
            Optional<Test> importedTest = Test.importNew(Arrays.stream(testcase.split(TESTCASE_LINE_DELIMITER)).toList());
            importedTest.ifPresent((test) -> tests.add(test));
        }
        return getTests();
    }

    /**
     * A.
     * @param current
     * @return A.
     */
    public Optional<Response> getNextResponse(Interaction current) {
        for (Test test : tests) {
            Optional<Response> response = test.nextResponseIfMatches(current);
            if (response.isPresent()) {
                return response;
            }
        }
        return Optional.empty();
    }

}
