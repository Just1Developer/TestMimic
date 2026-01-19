package net.justonedev.history;

import net.justonedev.Interaction;
import net.justonedev.Query;

import java.util.ArrayList;
import java.util.List;

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

    private record Entry(String value, EntryType type) {
    }

    private enum EntryType {
        QUERY, RESPONSE;
    }
}
