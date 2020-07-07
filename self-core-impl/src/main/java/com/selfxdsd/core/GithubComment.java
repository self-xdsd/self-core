package com.selfxdsd.core;

import com.selfxdsd.api.Comment;

import javax.json.JsonObject;

/**
 * Github Comment.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
final class GithubComment implements Comment {

    /**
     * Comment JSON as returned by Github's API.
     */
    private final JsonObject json;

    /**
     * Ctor.
     * @param json Comment JSON as returned by Github's API.
     */
    GithubComment(final JsonObject json) {
        this.json = json;
    }

    @Override
    public String commentId() {
        return String.valueOf(this.json.getInt("id"));
    }

    @Override
    public String body() {
        return this.json.getString("body");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
