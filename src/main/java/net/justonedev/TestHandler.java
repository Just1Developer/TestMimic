package net.justonedev;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class TestHandler {

    private final Interaction currentInteraction;

    public TestHandler(String[] args) {
        currentInteraction = new Interaction(args);
    }

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
                    System.out.println("Error: Unknown Testcase.");
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
