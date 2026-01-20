package net.justonedev.testing;

import net.justonedev.history.History;
import net.justonedev.query.Query;
import net.justonedev.response.Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A.
 * @author uwwfh
 */
public final class Test {
    private static final String TESTCASE_COMMENT_SYMBOL = "#";
    private static final String TESTCASE_METADATA_DELIMITER_PATTERN = "^-+$";
    private static final String TESTCASE_METADATA_NAME_PREFIX = "name:";
    private static final String TESTCASE_METADATA_DESCRIPTION_PREFIX = "comment:";
    private static final String TESTCASE_METADATA_ARGUMENTS_PREFIX = "args:";
    private static final String TESTCASE_ARGUMENTS_DELIMITER = " ";
    private static final String TESTCASE_INPUT_PREFIX = ">";

    private static final int DEFAULT_ARGUMENTS_ARRAY_LENGTH = 0;
    private static final int TESTCASE_LINE_INPUT_INDEX = 0;

    private static final String REGEX_PREFIX = "!R!";
    private static final String REGEX_PREFIX_ALT = "!A!!R!";
    private static final Map<String, String> REGEX_REPLACEMENTS = Map.of(
            "\\$", "$",
            ".*", "Any String",
            "^", ""
    );
    private static final Map<String, String> REGEX_REPLACEMENTS_FIRST_LAYER = Map.of(
            "\\\\d+?", "1",
            "\\\\s+?", " ",
            "(\\(\\?![^)]*\\))", "",    // Detect and remove negative lookahead groups
            "[$]$", ""
    );
    private static final Map<String, String> REGEX_REPLACEMENTS_SECOND_LAYER = Map.of(
            "[()?!^]", ""
    );

    private final String[] arguments;
    private final History history;

    private Test(String[] arguments, List<Query> interaction) {
        this.arguments = Arrays.copyOf(arguments, arguments.length);
        this.history = new History(interaction);
    }

    // todo private
    /**
     * A.
     * @param interaction
     * @return A.
     */
    public boolean matchesInteraction(Interaction interaction) {
        return Arrays.equals(interaction.getArguments(), this.arguments)
                && new History(interaction.getHistory()).isPrefixOfOther(this.history);
    }

    /**
     * A.
     * @param interaction
     * @return A.
     */
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

    /**
     * A.
     * @param file
     * @return A.
     */
    public static Optional<Test> importNew(File file) {
        List<String> lines;
        try {
            lines = readFilePrimitive(file);
        } catch (FileNotFoundException e) {
            // No handling needed.
            return Optional.empty();
        }
        return importNew(lines);
    }

    /**
     * A.
     * @param lines
     * @return A.
     */
    public static Optional<Test> importNew(List<String> lines) {
        boolean processingInput = false;
        String[] args = new String[DEFAULT_ARGUMENTS_ARRAY_LENGTH];

        String inputBuffer = null;
        List<Query> queries = new ArrayList<>();

        for (String untrimmedLine : lines) {
            String line = untrimmedLine;
            if (line.matches(TESTCASE_METADATA_DELIMITER_PATTERN)) {
                processingInput = true;
                continue;
            }
            if (!processingInput) {
                if (line.startsWith(TESTCASE_METADATA_NAME_PREFIX) || line.startsWith(TESTCASE_METADATA_DESCRIPTION_PREFIX)) {
                    continue;
                }
                if (line.startsWith(TESTCASE_METADATA_ARGUMENTS_PREFIX)) {
                    String argumentValues = line.replaceFirst(TESTCASE_METADATA_ARGUMENTS_PREFIX, "").trim();
                    if (!argumentValues.isEmpty()) {
                        args = argumentValues.split(TESTCASE_ARGUMENTS_DELIMITER);
                    }
                }
                continue;
            }

            if (line.startsWith(TESTCASE_COMMENT_SYMBOL)) {
                continue;
            }
            line = line.split(TESTCASE_COMMENT_SYMBOL)[TESTCASE_LINE_INPUT_INDEX];

            if (line.startsWith(REGEX_PREFIX) || line.startsWith(REGEX_PREFIX_ALT)) {
                line = primitiveRegexReplace(line);
            }
            if (line.startsWith(TESTCASE_INPUT_PREFIX)) {
                // Input
                String input = line.substring(TESTCASE_INPUT_PREFIX.length()).trim();
                if (inputBuffer != null) {
                    queries.add(Query.request(inputBuffer));
                }
                inputBuffer = input;
            // Output
            } else if (inputBuffer == null) {
                queries.add(Query.reply(line));
            } else {
                queries.add(Query.requestReply(inputBuffer, line));
                inputBuffer = null;
            }
        }
        if (inputBuffer != null) {
            queries.add(Query.request(inputBuffer));
        }
        return Optional.of(new Test(args, queries));
    }

    private static String primitiveRegexReplace(String line) {
        String replaced = line.replaceFirst(REGEX_PREFIX_ALT, "").replaceFirst(REGEX_PREFIX, "");
        for (var entry : REGEX_REPLACEMENTS.entrySet()) {
            replaced = replaced.replace(entry.getKey(), entry.getValue());
        }
        for (var entry : REGEX_REPLACEMENTS_FIRST_LAYER.entrySet()) {
            replaced = replaced.replaceAll(entry.getKey(), entry.getValue());
        }
        for (var entry : REGEX_REPLACEMENTS_SECOND_LAYER.entrySet()) {
            replaced = replaced.replaceAll(entry.getKey(), entry.getValue());
        }
        return replaced;
    }

    private static List<String> readFilePrimitive(File file) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        return reader.lines().toList();
    }
}
