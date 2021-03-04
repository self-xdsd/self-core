package com.selfxdsd.core;

import com.selfxdsd.api.Comment;

import javax.json.JsonObject;

/**
 * Bitbucket Comment.
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
final class BitbucketComment implements Comment {

    /**
     * Comment JSON as returned by Bitbucket's API.
     */
    private final JsonObject json;

    /**
     * Ctor.
     * @param json Comment JSON as returned by Bitbucket's API.
     */
    BitbucketComment(final JsonObject json) {
        this.json = json;
    }

    @Override
    public String commentId() {
        return String.valueOf(this.json.getInt("id"));
    }

    /**
     * {@inheritDoc}
     * <br/>
     * Since Bitbucket 2.0 doesn't reveal the `username` anymore in their json
     * responses, we will extract the `account_id` as username from `author`
     * section.
     */
    @Override
    public String author() {
        return this.json.getJsonObject("user").getString("account_id");
    }

    @Override
    public String body() {
        return this.json.getJsonObject("content").getString("raw");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}