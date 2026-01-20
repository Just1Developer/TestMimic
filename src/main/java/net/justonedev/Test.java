package net.justonedev;

import net.justonedev.history.History;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Test {
    private static final String REGEX_PREFIX = "!R!";
    private static final Map<String, String> REGEX_REPLACEMENTS = Map.of(
            "\\$", "$",
            ".*", "Any String"
    );
    private static final Map<String, String> REGEX_REPLACEMENTS_ALL = Map.of(
            "\\\\d+?", "1",
            "\\\\s+?", " "
    );

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
            boolean processingInput = false;
            String[] args = new String[0];

            String inputBuffer = null;
            List<Query> queries = new ArrayList<>();

            for (String untrimmedLine : lines) {
                String line = untrimmedLine.trim();
                if (line.matches("^-+$")) {
                    processingInput = true;
                    continue;
                }
                if (!processingInput) {
                    if (line.startsWith("name:") || line.startsWith("comment:")) {
                        continue;
                    }
                    if (line.startsWith("args:")) {
                        String argumentValues = line.replaceFirst("args:", "").trim();
                        if (!argumentValues.isEmpty()) {
                            args = argumentValues.split(" ");
                        }
                    }
                    continue;
                }

                if (line.startsWith(REGEX_PREFIX)) {
                    line = primitiveRegexReplace(line);
                }
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

    private static String primitiveRegexReplace(String line) {
        String replaced = line.replaceFirst(REGEX_PREFIX, "");
        for (var entry : REGEX_REPLACEMENTS.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }
        for (var entry : REGEX_REPLACEMENTS_ALL.entrySet()) {
            replaced = replaced.replaceAll(entry.getKey(), entry.getValue());
        }
        return replaced;
    }

    private static List<String> readFilePrimitive(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().toList();
    }
}
