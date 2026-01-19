package net.justonedev;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TestRegistry {

    private static final String ERROR_FOLDER_INVALID = "Error, the specified folder is not valid (does not exist or is not folder): %s";

    private final File testFolder;

    private List<Test> tests;

    public TestRegistry(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException(ERROR_FOLDER_INVALID.formatted(folder.getAbsolutePath()));
        }
        this.testFolder = folder;
    }

    public List<Test> getTests() {
        if (!isLoaded()) {
            return List.of();
        }
        return new ArrayList<>(tests);
    }

    public boolean isLoaded() {
        return tests != null;
    }

    public List<Test> loadTests() {
        this.tests = new ArrayList<>();
        for (File file : Objects.requireNonNull(testFolder.listFiles((dir, name) -> name.endsWith(".txt")))) {
            Optional<Test> importedTest = Test.importNew(file);
            importedTest.ifPresent((test) -> tests.add(test));
        }
        return getTests();
    }

    public Optional<Response> getNextResponse(Interaction current) {
        for (Test test : tests) {
            Optional<Response> response = test.nextResponseIfMatches(current);
            if (response.isPresent()) return response;
        }
        return Optional.empty();
    }

}
