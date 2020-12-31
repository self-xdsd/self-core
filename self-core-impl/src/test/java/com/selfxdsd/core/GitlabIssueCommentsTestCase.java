/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core;

import com.selfxdsd.api.Comment;
import com.selfxdsd.api.Comments;
import com.selfxdsd.core.mock.MockJsonResources;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link GitlabIssueComments}.
 *
 * @since 0.0.45
 */
public final class GitlabIssueCommentsTestCase {

    /**
     * GitlabIssueComments fails on erroneous response.
     */
    @Test(expected = IllegalStateException.class)
    public void failsCorrectly() {
        final Comments comments = new GitlabIssueComments(
            URI.create(
                "https://gitlab.com/api/v4/projects/23128361/issues/1/notes"
            ),
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_INTERNAL_ERROR,
                    Json.createObjectBuilder().build()
                )
            )
        );
        comments.iterator().next();
    }

    /**
     * GitlabIssueComments can receive a comment in JSON format.
     */
    @Test
    public void receivesCommentFromJson() {
        final String response =
            "[{\"id\":467538366,"
                + "\"type\":null,"
                + "\"body\":\"A comment\","
                + "\"attachment\":null,"
                + "\"author\":{"
                + "\"id\":6426178,"
                + "\"name\":\"andreoss\","
                + "\"username\":\"andreoss\","
                + "\"state\":\"active\","
                + "\"avatar_url\":\"https://a\","
                + "\"web_url\":\"https://gitlab.com/andreoss\"},"
                + "\"created_at\":\"2020-12-15T20:25:59.810Z\","
                + "\"updated_at\":\"2020-12-15T20:25:59.810Z\","
                + "\"system\":false,"
                + "\"noteable_id\":76103621,"
                + "\"noteable_type\":\"Issue\","
                + "\"resolvable\":false,"
                + "\"confidential\":false,"
                + "\"noteable_iid\":1,\"commands_changes\":{}}"
                + "]";
        final Comments comments = new GitlabIssueComments(
            URI.create(
                "https://gitlab.com/api/v4/projects/23128361/issues/1/notes"
            ),
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_OK,
                    Json.createReaderFactory(Map.of())
                        .createReader(new StringReader(response))
                        .readArray()
                )
            )
        );
        MatcherAssert.assertThat(
            comments,
            Matchers.hasItem(
                new IsComment("467538366", "andreoss", "A comment")
            )
        );
    }

    /**
     * GitlabIssueComments can post a comment.
     */
    @Test
    public void canPostComment() {
        final URI commentsUri = URI.create(
            "https://gitlab.com/api/v4/projects/23128361/issues/1/notes"
        );
        final Comments comments = new GitlabIssueComments(
            commentsUri,
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getUri(),
                        Matchers.equalTo(commentsUri)
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("body", "thanks :)")
                                .build()
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        Json.createObjectBuilder()
                            .add("id", "123")
                            .add("body", "thanks :)")
                            .build()
                    );
                }
            )
        );
        Comment comment = comments.post("thanks :)");
        MatcherAssert.assertThat(
            comment,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabIssueComment.class)
            )
        );
        MatcherAssert.assertThat(
            comment.json(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("id", "123")
                    .add("body", "thanks :)")
                    .build()
            )
        );
    }

    /**
     * GitlabIssueComments post may fail.
     */
    @Test(expected = IllegalStateException.class)
    public void postCanFail() {
        new GitlabIssueComments(
            URI.create("test/uri"),
            new MockJsonResources(
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_INTERNAL_ERROR,
                    JsonValue.NULL
                )
            )
        ).post("nothing!");
    }

    /**
     * Create a GitlabComment from received JSON.
     */
    @Test
    public void canCreateCommentFromJson() {
        final JsonObject json = Json.createObjectBuilder()
            .add("id", "123")
            .add("body", "thanks :)")
            .build();

        final Comment comment = new GitlabIssueComments(
            URI.create("test/uri"),
            Mockito.mock(JsonResources.class)
        ).received(json);

        MatcherAssert.assertThat(
            comment,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabIssueComment.class)
            )
        );
        MatcherAssert.assertThat(
            comment.json(),
            Matchers.equalTo(json)
        );
    }
}
