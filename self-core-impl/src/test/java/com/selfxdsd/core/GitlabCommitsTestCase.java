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

import com.selfxdsd.api.Commit;
import com.selfxdsd.api.Commits;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GitlabCommits}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.44
 */
public final class GitlabCommitsTestCase {

    /**
     * We cannot iterate over all of them for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final Commits commits = new GitlabCommits(
            Mockito.mock(JsonResources.class),
            URI.create("commits_uri"),
            Mockito.mock(Storage.class)
        );
        commits.iterator();
    }

    /**
     * GitlabCommits.getCommit(ref) can return a found commit (status 200 OK).
     */
    @Test
    public void getFoundCommit() {
        final String repoUri = "https://gitlab.com/api/v4/projects/id";
        final Repo repo = new GitlabRepo(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(repoUri + "/repository/commits/123")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("id", 123)
                            .add("title", "test commit")
                            .build()
                    );
                }
            ),
            URI.create(repoUri),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        );
        final Commit found = repo.commits().getCommit("123");
        MatcherAssert.assertThat(
            found,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabCommit.class)
            )
        );
        MatcherAssert.assertThat(
            found.json(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                .add("id", 123)
                .add("title", "test commit")
                .build()
            )
        );
    }

    /**
     * GitlabCommit.getCommit(ref) can return null if the commit is not found.
     */
    @Test
    public void getNullCommit() {
        final Commits commits = new GitlabCommits(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("commits_uri/bad_ref")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        JsonValue.NULL
                    );
                }
            ),
            URI.create("commits_uri"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits.getCommit("bad_ref");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * GitlabCommit.getCommit(ref) throws an exception.
     * if the response code is neither 200 nor 404
     */
    @Test(expected = IllegalStateException.class)
    public void getCommitThrowsException() {
        final Commits commits = new GitlabCommits(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("commits_uri/123")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_INTERNAL_ERROR,
                        JsonValue.NULL
                    );
                }
            ),
            URI.create("commits_uri"),
            Mockito.mock(Storage.class)
        );
        commits.getCommit("123");
    }

    /**
     * GithubCommits can return the latest commit.
     */
    @Test
    public void getLatestCommit() {
        final Commits commits = new GitlabCommits(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("commits_uri")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createArrayBuilder().add(
                            Json.createObjectBuilder()
                                .add("short_id", "123")
                                .build()
                        ).build()
                    );
                }
            ),
            URI.create("commits_uri"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits.latest();
        MatcherAssert.assertThat(
            found,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabCommit.class)
            )
        );
        MatcherAssert.assertThat(
            found.json().getString("short_id"),
            Matchers.equalTo("123")
        );
    }

    /**
     * GitlabCommits.latest() throws ISE if the HTTP response is not 200 OK.
     */
    @Test(expected = IllegalStateException.class)
    public void getLatestCommitThrowsIse() {
        final Commits commits = new GitlabCommits(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("commits_uri")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        JsonValue.NULL
                    );
                }
            ),
            URI.create("commits_uri"),
            Mockito.mock(Storage.class)
        );
        commits.latest();
    }

}
