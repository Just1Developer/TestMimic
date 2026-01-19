package net.justonedev;

import java.util.List;
import java.util.Scanner;

public class TestHandler {

    private final Interaction currentInteraction;

    public TestHandler(String[] args) {
        currentInteraction = new Interaction(args);
    }

    public void handle(List<Test> tests) {
        try (Scanner scanner = new Scanner(System.in)) {
            String line;
            while ((line = scanner.nextLine()) != null) {
                currentInteraction.addQuery(Query.request(line));
                System.out.println(tests.stream().anyMatch(test -> test.matchesInteraction(currentInteraction)));
            }
        }
    }
}
