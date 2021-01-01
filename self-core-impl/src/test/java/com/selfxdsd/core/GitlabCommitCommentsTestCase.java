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
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GitlabCommitComments}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.49
 */
public final class GitlabCommitCommentsTestCase {

    /**
     * GitlabCommitComments can receive a comment in JSON format.
     */
    @Test
    public void receivesCommentFromJson(){
        final Comments comments = new GitlabCommitComments(
            URI.create("../projects/id/repository/commits/sha/comments"),
            Mockito.mock(JsonResources.class)
        );
        final Comment received = comments.received(
            Json.createObjectBuilder()
                .add("note", "test comment")
                .build()
        );
        MatcherAssert.assertThat(
            received,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabCommitComment.class)
            )
        );
        MatcherAssert.assertThat(
            received.body(),
            Matchers.equalTo("test comment")
        );
    }

    /**
     * GitlabCommitComments can post a comment.
     */
    @Test
    public void postsCommentOk() {
        final GitlabCommit commit = new GitlabCommit(
            URI.create("../projects/id/repository/commits/sha"),
            Mockito.mock(JsonObject.class),
            Mockito.mock(Storage.class),
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("note", "test comment")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "../projects/id/repository/commits/sha/comments"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        Json.createObjectBuilder()
                            .add("note", "test comment")
                            .build()
                    );
                }
            )
        );
        final Comments comments = commit.comments();
        final Comment posted = comments.post("test comment");
        MatcherAssert.assertThat(
            posted.body(),
            Matchers.equalTo("test comment")
        );
    }

    /**
     * GitlabCommitComments complains if the comment cannot be posted.
     */
    @Test(expected = IllegalStateException.class)
    public void postCommentCanFail() {
        final Comments comments = new GitlabCommitComments(
            URI.create("../projects/id/repository/commits/sha/comments"),
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("note", "test comment")
                                .build()
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        JsonObject.NULL
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
        final Comments comments = new GitlabCommitComments(
            URI.create("../projects/id/repository/commits/sha/comments"),
            Mockito.mock(JsonResources.class)
        );
        comments.iterator();
    }
}
