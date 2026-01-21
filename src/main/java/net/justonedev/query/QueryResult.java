package net.justonedev.query;

/**
 * The result of a query. Queries can simply continue the execution of the program,
 * or they can indicate that the interaction is over and the program should quit.
 * @author uwwfh
 */
public enum QueryResult {
    /**
     * The query is intermediate, and the program should continue to execute.
     */
    CONTINUE,
    /**
     * The program should quit after this.
     */
    QUIT;
}
