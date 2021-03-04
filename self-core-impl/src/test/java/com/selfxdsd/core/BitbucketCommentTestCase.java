package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link BitbucketComment}.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public final class BitbucketCommentTestCase {

    /**
     * Bitbucket Comment can return its ID.
     */
    @Test
    public void returnsId() {
        final Comment comment = new BitbucketComment(this.createJsonComment());
        MatcherAssert.assertThat(comment.commentId(),
            Matchers.equalTo("10006600"));
    }

    /**
     * Bitbucket Comment can return its author.
     */
    @Test
    public void returnsAuthor() {
        final Comment comment = new BitbucketComment(this.createJsonComment());
        MatcherAssert.assertThat(
            comment.author(),
            Matchers.equalTo("601e661dcd564b00686f4e4b")
        );
    }

    /**
     * Bitbucket Comment can return its body.
     */
    @Test
    public void returnsBody() {
        final Comment comment = new BitbucketComment(this.createJsonComment());
        MatcherAssert.assertThat(comment.body(),
            Matchers.equalTo("This is a comment"));
    }

    /**
     * Bitbucket Comment can return its json object.
     */
    @Test
    public void returnsAsJsonFormat() {
        JsonObject json = this.createJsonComment();
        final Comment comment = new BitbucketComment(json);
        MatcherAssert.assertThat(comment.json(), Matchers.is(json));
    }

    /**
     * Creates a Comment as Json.
     * @return JsonObject.
     */
    private JsonObject createJsonComment() {
        return Json.createObjectBuilder()
            .add("content", Json.createObjectBuilder()
                .add("raw", "This is a comment")
                .build())
            .add("user", Json.createObjectBuilder()
                .add("account_id", "601e661dcd564b00686f4e4b")
                .build())
            .add("id", 10006600)
            .build();
    }
}