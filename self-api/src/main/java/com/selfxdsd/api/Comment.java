package com.selfxdsd.api;

import javax.json.JsonObject;

/**
 * Comment related to a particular Provider resource (e.g Issue).
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public interface Comment {

    /**
     * Comment ID.
     * @return String.
     */
    String commentId();

    /**
     * Author's username.
     * @return String.
     */
    String author();

    /**
     * Comment's content.
     * @return String
     */
    String body();

    /**
     * The Comment in JSON format as returned by the provider's API.
     * @return JsonObject.
     */
    JsonObject json();
}
