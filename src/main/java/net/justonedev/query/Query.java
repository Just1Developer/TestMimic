package net.justonedev.query;

/**
 * A.
 * @author uwwfh
 */
public final class Query {

    private static final String QUIT_COMMAND_NAME = "quit";

    private final String query;
    private final String reply;
    private final QueryResult result;

    private Query(String query, String reply, QueryResult result) {
        this.query = query;
        this.reply = reply;
        this.result = result;
    }

    /**
     * A.
     * @return A.
     */
    public boolean isQuit() {
        return hasQuery() && getQuery().equals(QUIT_COMMAND_NAME) && !hasReply();
    }

    /**
     * A.
     * @return A.
     */
    public String getQuery() {
        return query;
    }

    /**
     * A.
     * @return A.
     */
    public String getReply() {
        return reply == null ? "" : reply;
    }

    /**
     * A.
     * @return A.
     */
    public QueryResult getQueryResult() {
        return result;
    }

    /**
     * A.
     * @return A.
     */
    public boolean hasQuery() {
        return query != null;
    }

    /**
     * A.
     * @return A.
     */
    public boolean hasReply() {
        return reply != null;
    }

    /**
     * A.
     * @param query
     * @param reply
     * @return A.
     */
    public static Query requestReply(String query, String reply) {
        return new Query(query, reply, QueryResult.CONTINUE);
    }

    /**
     * A.
     * @param query
     * @return A.
     */
    public static Query request(String query) {
        if (query.equals(QUIT_COMMAND_NAME)) {
            return Query.quit();
        }
        return new Query(query, null, QueryResult.CONTINUE);
    }

    /**
     * A.
     * @param reply
     * @return A.
     */
    public static Query reply(String reply) {
        return new Query(null, reply, QueryResult.CONTINUE);
    }

    /**
     * A.
     * @return A.
     */
    private static Query quit() {
        return new Query(QUIT_COMMAND_NAME, null, QueryResult.QUIT);
    }
}
