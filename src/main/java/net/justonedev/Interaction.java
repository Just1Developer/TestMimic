package net.justonedev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Interaction {
    private final String[] arguments;
    private final List<Query> history;

    public Interaction(String[] args) {
        this.arguments = Arrays.copyOf(args, args.length);
        history = new ArrayList<>();
    }

    public void addQuery(Query query) {
        history.add(query);
    }

    public List<Query> getHistory() {
        return new ArrayList<>(history);
    }

    public String[] getArguments() {
        return Arrays.copyOf(arguments, arguments.length);
    }
}
