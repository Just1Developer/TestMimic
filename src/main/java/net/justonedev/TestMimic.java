package net.justonedev;

import net.justonedev.testing.TestHandler;
import net.justonedev.testing.TestRegistry;

/**
 * A.
 * @author uwwfh
 */
public final class TestMimic {

    private TestMimic() { }

    /**
     * A.
     * @param args
     */
    public static void main(String[] args) {
        TestRegistry testRegistry = new TestRegistry(Testcases.TESTS);
        testRegistry.loadTests();
        TestHandler handler = new TestHandler(args);
        handler.handle(testRegistry);
    }
}