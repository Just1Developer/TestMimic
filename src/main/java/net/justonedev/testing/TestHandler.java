package net.justonedev.testing;

import net.justonedev.query.Query;
import net.justonedev.response.Response;
import net.justonedev.response.ResponseType;

import java.util.Optional;
import java.util.Scanner;

/**
 * A.
 * @author uwwfh
 */
public class TestHandler {

    private static final String ERROR_MESSAGE_NO_MATCHING_TEST = "Error: Unknown Testcase.";

    private final Interaction currentInteraction;

    /**
     * A.
     * @param args
     */
    public TestHandler(String[] args) {
        currentInteraction = new Interaction(args);
    }

    /**
     * A.
     * @param testRegistry
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
