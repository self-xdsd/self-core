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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link BitbucketCommitComments}.
 * @author criske
 * @version $Id$
 * @since 0.0.67
 */
public final class BitbucketCommentsTestCase {

    /**
     * BitbucketCommitComments can receive a comment in JSON format.
     */
    @Test
    public void receivesCommentFromJson(){
        final Comments comments = new BitbucketCommitComments(
            URI.create(
                "https://bitbucket.org/!api/2.0/repositories/crisketm"
                    + "/my-super-repo/commit"
                    + "/84899952ccf723fe5e4306aac2c857f05ef4686a/comments"
            ),
            Mockito.mock(JsonResources.class)
        );
        final Comment received = comments.received(
            this.createJsonComment("some comment here")
        );
        MatcherAssert.assertThat(
            received,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(BitbucketComment.class)
            )
        );
        MatcherAssert.assertThat(
            received.body(),
            Matchers.equalTo("some comment here")
        );
        MatcherAssert.assertThat(
            received.author(),
            Matchers.equalTo("601e661dcd564b00686f4e4b")
        );
        MatcherAssert.assertThat(
            received.commentId(),
            Matchers.equalTo("10006600")
        );
    }

    /**
     * BitbucketCommitComments can post a comment.
     */
    @Test
    public void postsCommentOk() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_CREATED,
                this.createJsonComment("test comment")
            )
        );
        final Comments comments = new BitbucketCommitComments(
            URI.create(
                "https://bitbucket.org/!api/2.0/repositories/crisketm"
                    + "/my-super-repo/commit"
                    + "/84899952ccf723fe5e4306aac2c857f05ef4686a/comments"
            ),
            resources
        );
        final Comment posted = comments.post("test comment");
        final MockJsonResources.MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            req.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("content", Json.createObjectBuilder()
                        .add("raw", "test comment")
                        .build())
                    .build()
            )
        );
        MatcherAssert.assertThat(
            req.getUri().toString(),
            Matchers.equalTo(
                "https://bitbucket.org/!api/2.0/repositories/crisketm"
                    + "/my-super-repo/commit"
                    + "/84899952ccf723fe5e4306aac2c857f05ef4686a/comments"
            )
        );
        MatcherAssert.assertThat(
            posted.body(),
            Matchers.equalTo("test comment")
        );
    }

    /**
     * BitbucketCommitComments complains if the comment cannot be posted.
     */
    @Test(expected = IllegalStateException.class)
    public void postsCommentNotFound() {
        final MockJsonResources resources = new MockJsonResources(
            new AccessToken.Bitbucket("Bitbucket123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                JsonValue.NULL
            )
        );
        final Comments comments = new BitbucketCommitComments(
            URI.create(
                "https://bitbucket.org/!api/2.0/repositories/crisketm"
                    + "/my-super-repo/commit"
                    + "/84899952ccf723fe5e4306aac2c857f05ef4686a/comments"
            ),
            resources
        );
        comments.post("test comment");
    }

    /**
     * We cannot iterate over all of them for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final Comments comments = new BitbucketCommitComments(
            URI.create(
                "https://bitbucket.org/!api/2.0/repositories/crisketm"
                    + "/my-super-repo/commit"
                    + "/84899952ccf723fe5e4306aac2c857f05ef4686a/comments"
            ),
            Mockito.mock(JsonResources.class)
        );
        comments.iterator();
    }

    /**
     * Creates a Comment as Json.
     * @param body Comment body.
     * @return JsonObject.
     */
    private JsonObject createJsonComment(final String body) {
        return Json.createObjectBuilder()
            .add("content", Json.createObjectBuilder()
                .add("raw", body)
                .build())
            .add("user", Json.createObjectBuilder()
                .add("account_id", "601e661dcd564b00686f4e4b")
                .build())
            .add("id", 10006600)
            .build();
    }
}