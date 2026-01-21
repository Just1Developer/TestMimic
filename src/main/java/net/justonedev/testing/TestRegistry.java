package net.justonedev.testing;

import net.justonedev.response.Response;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The test registry is essentially a collection of tests that is loaded
 * by the TestRegistry at runtime after construction.
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
     * Creates a new TestRegistry with all testcases encoded as a single
     * string. The linebreaks in the string are entirely ignored and
     * immediately removed. Linebreaks in Files should be encoded with
     * {@code %n}, Testcases separated by {@code %T}. Spaces should be
     * encoded with the character {@code §}.
     *
     * @param testcases the entire list of testcases as a single string.
     */
    public TestRegistry(String testcases) {
        this.testFolder = null;
        this.testcases = testcases.replace(System.lineSeparator(), "")
                .replace(SPACE_ENCODING, SPACE_DECODING);
    }

    /**
     * Returns an unmodifiable list of all loaded tests. If the registry has not
     * been loaded yet, an empty list is returned. That list is also unmodifiable.
     * @return An unmodifiable list of tests.
     */
    public List<Test> getTests() {
        if (!isLoaded()) {
            return List.of();
        }
        return List.copyOf(tests);
    }

    /**
     * Returns if the TestRegistry has been loaded using the {@link TestRegistry#loadTests()} method.
     * @return True if the registry has been loaded, false if not.
     */
    public boolean isLoaded() {
        return tests != null;
    }

    /**
     * Loads all tests from the registry, so from the given string (or folder) and stores them internally.
     * Returns an unmodifiable copy of the loaded tests.
     * @return an unmodifiable copy of the loaded tests.
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
     * Gets the next response for an interaction by matching the interaction with
     * every loaded test case and returning its response, if present. The reponse
     * also contains if the reply should be printed or if there is no response.
     * <p>
     *     If no test case matches, returns an empty optional.
     * </p>
     * @param current the current interaction.
     * @return The next response for the given interaction, empty if no test matches.
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
