package net.justonedev.history;

import net.justonedev.query.Query;
import net.justonedev.response.Response;
import net.justonedev.response.ResponseType;
import net.justonedev.testing.Interaction;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * A.
 * @author uwwfh
 */
public class History {

    private static final int HISTORY_COMPARISON_BEGIN = 0;

    private final List<Entry> history;

    /**
     * A.
     * @param interaction
     */
    public History(Interaction interaction) {
        history = new ArrayList<>();
        for (Query query : interaction.getHistory()) {
            if (query.hasQuery()) {
                history.add(new Entry(query.getQuery(), EntryType.QUERY));
            }
            if (query.hasReply()) {
                history.add(new Entry(query.getReply(), EntryType.RESPONSE));
            }
        }
    }

    /**
     * A.
     * @param interaction
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
     * A.
     * @param other
     * @return A.
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
     * A.
     * @return A.
     */
    public int size() {
        return history.size();
    }

    /**
     * A.
     * @param startIndex
     * @return A.
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
     * A.
     * @param value
     * @param type
     * @author uwwfh
     */
    private record Entry(String value, EntryType type) {
    }

    /**
     * A.
     * @author uwwfh
     */
    private enum EntryType {
        /**
         * A.
         */
        QUERY,
        /**
         * A.
         */
        RESPONSE;
    }
}
