package com.selfxdsd.core;

import com.selfxdsd.api.Comment;

import javax.json.JsonObject;

/**
 * Gitlab commit comment.
 *
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.49
 */
final class GitlabCommitComment implements Comment {

    /**
     * Comment JSON as returned by Gitlab's API.
     */
    private final JsonObject json;

    /**
     * Ctor.
     *
     * @param json Comment JSON as returned by Gitlab's API.
     */
    GitlabCommitComment(final JsonObject json) {
        this.json = json;
    }

    @Override
    public String commentId() {
        throw new UnsupportedOperationException(
            "GitLab Commit Comment has no ID"
        );
    }

    @Override
    public String author() {
        return this.json.getJsonObject("author").getString("username");
    }

    @Override
    public String body() {
        return this.json.getString("note");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
