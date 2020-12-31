package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link GitlabCommitComment}.
 *
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.49
 */
public final class GitlabCommitCommentTestCase {

    /**
     * Gitlab commit comment doesn't have an id.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void commitIdIsNotSupported() {
        new GitlabCommitComment(Mockito.mock(JsonObject.class)).commentId();
    }

    /**
     * Commit comment can return its author.
     */
    @Test
    public void canReturnItsAuthor() {
        final Comment comment = new GitlabCommitComment(
            Json.createObjectBuilder()
                .add(
                    "author",
                    Json.createObjectBuilder()
                        .add("username", "alilo")
                )
                .build()
        );
        MatcherAssert.assertThat(
            comment.author(),
            Matchers.equalTo("alilo")
        );
    }

    /**
     * Commit comment can return its body.
     */
    @Test
    public void canReturnItsBody() {
        final Comment comment = new GitlabCommitComment(
            Json.createObjectBuilder()
                .add("note", "test")
                .build()
        );
        MatcherAssert.assertThat(
            comment.body(),
            Matchers.equalTo("test")
        );
    }

    /**
     * Commit comment can return its original JsonObject.
     */
    @Test
    public void canReturnItsOriginalJson() {
        JsonObject json = Json.createObjectBuilder()
            .add("note", "This is a comment")
            .build();
        final Comment comment = new GitlabIssueComment(json);
        MatcherAssert.assertThat(
            comment.json(),
            Matchers.is(json)
        );
    }
}
