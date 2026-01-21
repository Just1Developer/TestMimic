package net.justonedev.response;

/**
 * The type of a response. Responses cannot differentiate between no response
 * and an empty response, so response types take care of that.
 * @author uwwfh
 */
public enum ResponseType {
    /**
     * The response has a string message.
     */
    STRING,
    /**
     * The response has no message. Any content in the message is to be disregarded.
     */
    NONE;
}
