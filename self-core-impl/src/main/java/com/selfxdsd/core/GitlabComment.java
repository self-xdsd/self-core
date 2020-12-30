package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import javax.json.JsonObject;

/**
 * Gitlab Comment.
 *
 * <a href="https://docs.gitlab.com/ee/api/notes.html#get-single-issue-note">Notes API</a>
 *
 * @author andreoss
 * @version $Id$
 * @since 0.0.45
 */
final class GitlabComment implements Comment {

    /**
     * Comment JSON as returned by Github's API.
     */
    private final JsonObject json;

    /**
     * Ctor.
     *
     * @param json Comment JSON as returned by Github's API.
     */
    GitlabComment(final JsonObject json) {
        this.json = json;
    }

    @Override
    public String commentId() {
        return String.valueOf(this.json.getInt("id"));
    }

    @Override
    public String author() {
        return this.json.getJsonObject("author").getString("username");
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
