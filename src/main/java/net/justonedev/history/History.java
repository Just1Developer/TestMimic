package net.justonedev.history;

import net.justonedev.query.Query;
import net.justonedev.response.Response;
import net.justonedev.response.ResponseType;
import net.justonedev.testing.Interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Represents a History of an Interaction. Does not bundle requests and follow-up responses
 * like {@link Query} does. Histories can be built from test cases and Interactions,
 * and can be compared by prefix.<br/>
 * The History still differentiates between request and response, so that differentiation
 * is also required in the Objects where the History is built from.
 *
 * <p>
 *     Histories are effectively immutable, though they can be modified using Reflection.
 *     This is not recommended and can lead to undefined behaviour while testing.
 * </p>
 *
 * @author uwwfh
 */
public class History {

    private static final int HISTORY_COMPARISON_BEGIN = 0;

    private final List<Entry> history;

    /**
     * Creates a new History from an Interaction. Properly copies queries
     * and responses and unbundles them in the correct order.
     *
     * @param interaction the command line interaction.
     */
    public History(Interaction interaction) {
        this(interaction.getHistory());
    }

    /**
     * Creates a new History from a list of queries. Properly copies queries
     * and responses and unbundles them in the correct order.
     *
     * @param interaction the list of queries to unbundle to parse the history from.
     */
    public History(List<Query> interaction) {
        history = new ArrayList<>();
        for (Query query : interaction) {
            if (query.hasQuery()) {
                history.add(new Entry(query.getQuery(), EntryType.QUERY));
            }
            if (query.hasReply()) {
                history.add(new Entry(query.getReply(), EntryType.RESPONSE));
            }
        }
    }

    /**
     * If the current history is a "prefix" of another given history. That is,
     * are the first n interactions the same for both histories, where n is
     * the entire size of this history.
     * <p>
     *     Returns false if the other history is shorter than the current history.
     * </p>
     * @param other The other history to compare to.
     * @return true if this history is a prefix or equal, false if not.
     */
    public boolean isPrefixOfOther(History other) {
        if (other.history.size() < history.size()) {
            return false;
        }
        for (int i = HISTORY_COMPARISON_BEGIN; i < history.size(); i++) {
            Entry entry = history.get(i);
            Entry otherEntry = other.history.get(i);
            if (!entry.equals(otherEntry)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the size of the History. Each message line contributes
     * 1 to the size, so a request reply is 2, and a request without reply is 1.
     * @return the size of the history.
     */
    public int size() {
        return history.size();
    }

    /**
     * Starting from a given index, returns the entire output after and including that
     * index as a Response with a single multiline string. It concatenates all entries
     * in the history at and past the index until an entry of type "query" is reached.
     * <p>
     *     The response contains additional information as to if any response is even
     *     present. In the case that there is an input at the given Index, the response
     *     will be an empty string but of {@link ResponseType#NONE}.
     * </p>
     *
     * @param startIndex The index of the first output line, usually 1 past the entry.
     * @return The response as {@link Response} with a multiline string.
     */
    public Response getConsecutiveOutputFromStartIndex(int startIndex) {
        List<Entry> list = new ArrayList<>();
        for (int index = startIndex; index < history.size() && history.get(index).type == EntryType.RESPONSE; index++) {
            list.add(history.get(index));
        }
        StringJoiner responseJoiner = new StringJoiner(System.lineSeparator());
        list.forEach(entry -> responseJoiner.add(entry.value));
        String response = responseJoiner.toString();
        ResponseType responseType;
        if (list.isEmpty()) {
            responseType = ResponseType.NONE;
        } else {
            responseType = ResponseType.STRING;
        }
        return new Response(response, responseType);
    }

    /**
     * An entry for the history. Stores its line as value and the type of the string,
     * meaning if it is an Input or Output (query or response).
     * @param value the line.
     * @param type the type, input or output.
     * @author uwwfh
     */
    private record Entry(String value, EntryType type) {
    }

    /**
     * The type of an {@link Entry} in the history. Indicates if the entry is an
     * input (query) or an output (response).
     * @author uwwfh
     */
    private enum EntryType {
        /**
         * The type for all queries, so interaction inputs by the user.
         */
        QUERY,
        /**
         * The type for all responses, so replies by the program.
         */
        RESPONSE;
    }
}
