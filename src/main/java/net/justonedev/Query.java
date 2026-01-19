package net.justonedev;

public class Query {

    private static final String QUIT_COMMAND_NAME = "quit";

    private final String query;
    private final String reply;
    private final QueryResult result;

    private Query(String query, String reply, QueryResult result) {
        this.query = query;
        this.reply = reply;
        this.result = result;
    }

    public boolean isQuit() {
        return hasQuery() && getQuery().equals(QUIT_COMMAND_NAME) && !hasReply();
    }

    public String getQuery() {
        return query;
    }

    public String getReply() {
        return reply == null ? "" : reply;
    }

    public QueryResult getQueryResult() {
        return result;
    }

    public boolean hasQuery() {
        return query != null;
    }

    public boolean hasReply() {
        return reply != null;
    }

    public static Query requestReply(String query, String reply) {
        return new Query(query, reply, QueryResult.CONTINUE);
    }

    public static Query request(String query) {
        if (query.equals(QUIT_COMMAND_NAME)) {
            return Query.quit();
        }
        return new Query(query, null, QueryResult.CONTINUE);
    }

    public static Query reply(String reply) {
        return new Query(null, reply, QueryResult.CONTINUE);
    }

    public static Query quit() {
        return new Query(QUIT_COMMAND_NAME, null, net.justonedev.QueryResult.QUIT);
    }
}
