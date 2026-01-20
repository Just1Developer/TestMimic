package net.justonedev;

public final class TestMimic {

    private static final String TESTS_FOLDER_PATH = "test/Blatt5";

    private TestMimic() { }

    public static void main(String[] args) {
        //args = new String[] { "argument1", "arg2" };

        TestRegistry testRegistry = new TestRegistry(TESTS_FOLDER_PATH);
        testRegistry.loadTests();
        TestHandler handler = new TestHandler(args);
        handler.handle(testRegistry);
    }
}