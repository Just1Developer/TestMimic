package net.justonedev.testing;

import net.justonedev.query.Query;
import net.justonedev.response.Response;
import net.justonedev.response.ResponseType;

import java.util.Optional;
import java.util.Scanner;

/**
 * The test handler handles an the currently active interaction with the given
 * arguments and a test registry.
 *
 * @author uwwfh
 */
public class TestHandler {

    private static final String ERROR_MESSAGE_NO_MATCHING_TEST = "Error: Unknown Testcase.";

    private final Interaction currentInteraction;

    /**
     * Creates a new test handler with the current command line arguments.
     * @param args the command line arguments.
     */
    public TestHandler(String[] args) {
        currentInteraction = new Interaction(args);
    }

    /**
     * Handles the interaction from start to finish, based on the provided tests loaded
     * in the given {@link TestRegistry}.<br/>
     * If an interaction occurs that is not known, so if no test case matches, the program
     * will terminate with an error message stating that this is an unknown test case.
     * <p>
     *     The registry must already have all test cases loaded.
     * </p>
     *
     * @param testRegistry The registry of loaded testcase.
     */
    public void handle(TestRegistry testRegistry) {
        try (Scanner scanner = new Scanner(System.in)) {
            String line;
            while ((line = scanner.nextLine()) != null) {
                Query query = Query.request(line);
                if (query.isQuit()) {
                    return;
                }
                currentInteraction.addQuery(query);
                Optional<Response> responseOpt = testRegistry.getNextResponse(currentInteraction);
                if (responseOpt.isEmpty()) {
                    System.out.println(ERROR_MESSAGE_NO_MATCHING_TEST);
                    return;
                }
                Response response = responseOpt.get();
                if (response.responseType() == ResponseType.STRING) {
                    for (String replyLine : response.response().split(System.lineSeparator())) {
                        currentInteraction.addQuery(Query.reply(replyLine));
                    }
                    System.out.println(response.response());
                }
            }
        }
    }
}
