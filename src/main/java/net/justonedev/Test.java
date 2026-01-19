package net.justonedev;

import net.justonedev.history.History;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Test {
    private final String[] arguments;
    private final History history;

    private Test(String[] arguments, List<Query> interaction) {
        this.arguments = Arrays.copyOf(arguments, arguments.length);
        this.history = new History(interaction);
    }

    public boolean matchesInteraction(Interaction interaction) {
        if (!Arrays.equals(interaction.getArguments(), this.arguments)) {
            return false;
        }
        return new History(interaction.getHistory()).isPrefixOfOther(this.history);
    }

    public Optional<Response> nextResponseIfMatches(Interaction interaction) {
        if (!this.matchesInteraction(interaction)) {
            return Optional.empty();
        }
        int previousSize = new History(interaction.getHistory()).size();
        if (previousSize >= history.size()) {
            // Should never happen
            return Optional.empty();
        }
        return Optional.of(history.getConsecutiveOutputFromStartIndex(previousSize));
    }

    public static Optional<Test> importNew(File file) {
        try {
            List<String> lines = readFilePrimitive(file);
            String[] args = lines.getFirst().isBlank() ? new String[0] : lines.getFirst().split(" ");
            String inputBuffer = null;
            List<Query> queries = new ArrayList<>();
            for (int i = 2; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.startsWith(">")) {
                    // Input
                    String input = line.substring(1).trim();
                    if (inputBuffer != null) {
                        queries.add(Query.request(inputBuffer));
                    }
                    inputBuffer = input;
                } else {
                    // Output
                    if (inputBuffer == null) {
                        queries.add(Query.reply(line));
                    } else {
                        queries.add(Query.requestReply(inputBuffer, line));
                        inputBuffer = null;
                    }
                }
            }
            if (inputBuffer != null) {
                queries.add(Query.request(inputBuffer));
            }
            return Optional.of(new Test(args, queries));
        } catch (FileNotFoundException e) {
            // No handling needed, file exists if we get here.
        }
        return Optional.empty();
    }

    private static List<String> readFilePrimitive(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().toList();
    }
}
