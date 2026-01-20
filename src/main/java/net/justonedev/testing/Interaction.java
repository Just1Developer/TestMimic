package net.justonedev.testing;

import net.justonedev.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A.
 * @author uwwfh
 */
public class Interaction {
    private final String[] arguments;
    private final List<Query> history;

    /**
     * A.
     * @param args
     */
    public Interaction(String[] args) {
        this.arguments = Arrays.copyOf(args, args.length);
        history = new ArrayList<>();
    }

    /**
     * A.
     * @param query
     */
    public void addQuery(Query query) {
        history.add(query);
    }

    /**
     * A.
     * @return A.
     */
    public List<Query> getHistory() {
        return new ArrayList<>(history);
    }

    /**
     * A.
     * @return A.
     */
    public String[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }
}
