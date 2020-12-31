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
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GithubCommitComments}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.31
 */
public final class GithubCommitCommentsTestCase {

    /**
     * GithubCommitComments can receive a comment in JSON format.
     */
    @Test
    public void receivesCommentFromJson(){
        final Comments comments = new GithubCommitComments(
            URI.create(
                "localhost/repos/octocat/Hello-World/commits/ref1/comments"
            ),
            Mockito.mock(JsonResources.class)
        );
        final Comment received = comments.received(
            Json.createObjectBuilder()
                .add("body", "some comment here")
                .build()
        );
        MatcherAssert.assertThat(
            received,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GithubComment.class)
            )
        );
        MatcherAssert.assertThat(
            received.body(),
            Matchers.equalTo("some comment here")
        );
    }

    /**
     * GithubCommitComments can post a comment.
     */
    @Test
    public void postsCommentOk() {
        final Comments comments = new GithubCommitComments(
            URI.create("http://localhost/repos/mihai/test/commits/ref1"),
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("body", "test comment")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits/ref1"
                            + "/comments"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        Json.createObjectBuilder()
                            .add("body", "test comment")
                            .build()
                    );
                }
            )
        );
        final Comment posted = comments.post("test comment");
        MatcherAssert.assertThat(
            posted.body(),
            Matchers.equalTo("test comment")
        );
    }

    /**
     * GithubCommitComments complains if the comment cannot be posted.
     */
    @Test(expected = IllegalStateException.class)
    public void postsCommentNotFound() {
        final Comments comments = new GithubCommitComments(
            URI.create("http://localhost/repos/mihai/test/commits/ref1"),
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("body", "test comment")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits/ref1"
                                + "/comments"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        comments.post("test comment");
    }

    /**
     * We cannot iterate over all of them for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final Comments comments = new GithubCommitComments(
            URI.create("local/repos/mihai/test/commits/ref1"),
            Mockito.mock(JsonResources.class)
        );
        comments.iterator();
    }
}
