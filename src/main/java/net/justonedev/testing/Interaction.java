package net.justonedev.testing;

import net.justonedev.history.History;
import net.justonedev.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents a modifiable command line interaction and keeps track of the history.
 * Since objects of {@link History} are static and immutable, does not keep
 * track of it. For comparison with test cases, you can create a new History using the interaction
 * as argument.
 * <p>
 *     In comparison to {@link Test}, this collection of queries can be modified and provides other functionality.
 *     Mainly used for the current CLI interaction.
 * </p>
 * @author uwwfh
 */
public class Interaction {
    private final String[] arguments;
    private final List<Query> history;

    /**
     * Creates a new interaction and stores a copy of the given command
     * line arguments.
     * @param args The command line arguments.
     */
    public Interaction(String[] args) {
        this.arguments = Arrays.copyOf(args, args.length);
        history = new ArrayList<>();
    }

    /**
     * Adds a new query to the history.
     * @param query the query to add.
     */
    public void addQuery(Query query) {
        history.add(query);
    }

    /**
     * Gets a copy of the entire history. Compared to {@link History},
     * this history is not atomic, meaning queries and responses may be bundled.
     * @return a copy of the history.
     */
    public List<Query> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * Gets a copy of the command line arguments.
     * @return a copy of the cli arguments.
     */
    public String[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }
}
