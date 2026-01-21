package net.justonedev.response;

/**
 * A record class for a response that stores the response as (possibly) multiline string
 * and a response type indicating the type.
 * @param response the response as string.
 * @param responseType the response type.
 *
 * @author uwwfh
 */
public record Response(String response, ResponseType responseType) {
}
