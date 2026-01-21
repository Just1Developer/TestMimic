package net.justonedev.query;

/**
 * This class is a query that may bundle a request (the query) with its reply.
 * Queries can exist with only a request or only a reply as well though.
 * <p>
 *     Static methods are used for instantiation.
 * </p>
 *
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
     * If the query is the simple "quit" command with no response.
     * @return If the query is quit.
     */
    public boolean isQuit() {
        return hasQuery() && getQuery().equals(QUIT_COMMAND_NAME) && !hasReply()
                || getQueryResult() == QueryResult.QUIT;
    }

    /**
     * Gets the query string.
     * <p>
     *     If there is no query, this returns {@code null}. Check with {@link Query#hasQuery()} beforehand.
     * </p>
     * @return The query if it exists, or {@code null}.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Gets the reply string.
     * <p>
     *     If there is no reply, this returns an empty string.
     *     You can check the existence of the reply with {@link Query#hasReply()}.
     * </p>
     * @return The reply or an empty string if no reply exists.
     */
    public String getReply() {
        return reply == null ? "" : reply;
    }

    /**
     * Gets the result of this query.
     * @return The query result.
     */
    public QueryResult getQueryResult() {
        return result;
    }

    /**
     * If a query or request is present.
     * @return true if a query is present, false if not.
     */
    public boolean hasQuery() {
        return query != null;
    }

    /**
     * If a reply is present.
     * @return true if a reply is present, false if not.
     */
    public boolean hasReply() {
        return reply != null;
    }

    /**
     * Creates a new {@link Query} consisting of a request and a reply.
     * @param query the query / request.
     * @param reply the reply.
     * @return The newly created query.
     */
    public static Query requestReply(String query, String reply) {
        return new Query(query, reply, QueryResult.CONTINUE);
    }

    /**
     * Creates a new {@link Query} consisting of only a request.
     * @param query the query / request.
     * @return The newly created query.
     */
    public static Query request(String query) {
        if (query.equals(QUIT_COMMAND_NAME)) {
            return Query.quit();
        }
        return new Query(query, null, QueryResult.CONTINUE);
    }

    /**
     * Creates a new {@link Query} consisting of only a reply.
     * @param reply the reply.
     * @return The newly created query.
     */
    public static Query reply(String reply) {
        return new Query(null, reply, QueryResult.CONTINUE);
    }

    /**
     * Creates a quit query. This query will qualify as quit query when
     * {@link Query#isQuit()} is called, and consists of a request that
     * equals the quit command name and no response.
     * @return a new quit query.
     */
    private static Query quit() {
        return new Query(QUIT_COMMAND_NAME, null, QueryResult.QUIT);
    }
}
