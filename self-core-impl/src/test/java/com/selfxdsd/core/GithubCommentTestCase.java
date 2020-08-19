package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link GithubComment}.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class GithubCommentTestCase {

    /**
     * Github Comment can return its ID.
     */
    @Test
    public void returnsId() {
        final Comment comment = new GithubComment(
            Json.createObjectBuilder().add("id", 1).build()
        );
        MatcherAssert.assertThat(comment.commentId(),
            Matchers.equalTo("1"));
    }

    /**
     * Github Comment can return its author.
     */
    @Test
    public void returnsAuthor() {
        final Comment comment = new GithubComment(
            Json.createObjectBuilder()
                .add("id", 1)
                .add("user", Json.createObjectBuilder().add("login", "mihai"))
                .build()
        );
        MatcherAssert.assertThat(
            comment.author(),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * Github Comment can return its body.
     */
    @Test
    public void returnsBody() {
        final Comment comment = new GithubComment(
            Json.createObjectBuilder()
                .add("id", 1)
                .add("body", "This is a comment")
                .build()
        );
        MatcherAssert.assertThat(comment.body(),
            Matchers.equalTo("This is a comment"));
    }

    /**
     * Github Comment can return its json object.
     */
    @Test
    public void returnsAsJsonFormat() {
        JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .add("body", "This is a comment")
            .build();
        final Comment comment = new GithubComment(json);
        MatcherAssert.assertThat(comment.json(), Matchers.is(json));
    }
}
