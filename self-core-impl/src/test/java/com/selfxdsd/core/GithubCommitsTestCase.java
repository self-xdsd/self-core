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

import java.net.HttpURLConnection;
import java.net.URI;
import javax.json.Json;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import com.selfxdsd.api.Commit;
import com.selfxdsd.api.Commits;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;

/**
 * Unit tests for {@link GithubCommits}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.31
 */
public final class GithubCommitsTestCase {

    /**
     * We cannot iterate over all of them for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final Commits commits = new GithubCommits(
            Mockito.mock(JsonResources.class),
            URI.create("local/repos/mihai/test/commits/ref1"),
            Mockito.mock(Storage.class)
        );
        commits.iterator();
    }

    /**
     * GithubCommit can return a found commit (status 200 OK).
     */
    @Test
    public void getFoundCommit() {
        final Commits commits = new GithubCommits(
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits/123"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("sha", "123")
                            .add(
                                "author",
                                Json.createObjectBuilder()
                                    .add("login", "mihai")
                            ).build()
                    );
                }
            ),
            URI.create("http://localhost/repos/mihai/test/commits"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits.getCommit("123");
        MatcherAssert.assertThat(
            found.author(),
            Matchers.equalTo("mihai")
        );
    }

    /**
     * GithubCommit can return null if the commit is not found.
     */
    @Test
    public void getNullCommit() {
        final Commits commits = new GithubCommits(
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits/123"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        422,
                        Json.createObjectBuilder().build()
                    );
                }
            ),
            URI.create("http://localhost/repos/mihai/test/commits"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits.getCommit("123");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * GithubCommit throws an exception if getCommit received 500 SERVER ERROR.
     */
    @Test(expected = IllegalStateException.class)
    public void getCommitThrowsException() {
        final Commits commits = new GithubCommits(
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits/123"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_INTERNAL_ERROR,
                        Json.createObjectBuilder().build()
                    );
                }
            ),
            URI.create("http://localhost/repos/mihai/test/commits"),
            Mockito.mock(Storage.class)
        );
        commits.getCommit("123");
    }

    /**
     * GithubCommits can return the latest commit.
     */
    @Test
    public void getLatestCommit() {
        final Commits commits = new GithubCommits(
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createArrayBuilder().add(
                            Json.createObjectBuilder()
                                .add("sha", "123")
                                .build()
                        ).build()
                    );
                }
            ),
            URI.create("http://localhost/repos/mihai/test/commits"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits.latest();
        MatcherAssert.assertThat(
            found.shaRef(),
            Matchers.equalTo("123")
        );
    }

    /**
     * GithubCommits.latest() throws ISE if the HTTP response is not 200 OK.
     */
    @Test(expected = IllegalStateException.class)
    public void getLatestCommitThrowsIse() {
        final Commits commits = new GithubCommits(
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "http://localhost/repos/mihai/test/commits"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        Json.createArrayBuilder().build()
                    );
                }
            ),
            URI.create("http://localhost/repos/mihai/test/commits"),
            Mockito.mock(Storage.class)
        );
        commits.latest();
    }

}
