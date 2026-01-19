package net.justonedev.history;

import net.justonedev.Interaction;
import net.justonedev.Query;
import net.justonedev.Response;
import net.justonedev.ResponseType;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class History {

    private List<Entry> history;

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

    public boolean isPrefixOfOther(History other) {
        if (other.history.size() < history.size()) {
            return false;
        }
        for (int i = 0; i < history.size(); i++) {
            Entry entry = history.get(i);
            Entry otherEntry = other.history.get(i);
            if (!entry.equals(otherEntry)) return false;
        }
        return true;
    }

    public int size() {
        return history.size();
    }

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

    private record Entry(String value, EntryType type) {
    }

    private enum EntryType {
        QUERY, RESPONSE;
    }
}
