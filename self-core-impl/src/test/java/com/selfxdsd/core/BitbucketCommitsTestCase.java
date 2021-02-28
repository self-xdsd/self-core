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
 * Unit tests for {@link BitbucketCommits}.
 * @author criske
 * @version $Id$
 * @since 0.0.31
 */
public final class BitbucketCommitsTestCase {

    /**
     * We cannot iterate over all of them for now.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void cannotIterate() {
        final Commits commits = new BitbucketCommits(
            Mockito.mock(JsonResources.class),
            URI.create("https://bitbucket.org/api/2.0/repositories"
                + "/crisketm/my-super-repo/commits"),
            Mockito.mock(Storage.class)
        );
        commits.iterator();
    }

    /**
     * BitbucketCommit can return a found commit (status 200 OK).
     */
    @Test
    public void getFoundCommit() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder()
                    .add("values", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add("hash",
                                "84899952ccf723fe5e4306aac2c857f05ef4686a")
                            .add("author", Json.createObjectBuilder()
                                .add("account_id",
                                    "601e661dcd564b00686f4e4b")
                                .build())
                            .build())
                        .build()
                    )
                    .build()
            )
        );
        final Commits commits = new BitbucketCommits(
            resources,
            URI.create("https://bitbucket.org/api/2.0/repositories"
                + "/crisketm/my-super-repo/commits"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits
            .getCommit("13f74048cd8b4c2fdafdf0d45771c1cf73b998de");
        final MockJsonResources.MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(
            req.getUri().toString(),
            Matchers.equalTo(
                "https://bitbucket.org/api/2.0/repositories"
                    + "/crisketm/my-super-repo/commits"
                    + "/13f74048cd8b4c2fdafdf0d45771c1cf73b998de"
            )
        );
        MatcherAssert.assertThat(
            found.author(),
            Matchers.equalTo("601e661dcd564b00686f4e4b")
        );
        MatcherAssert.assertThat(
            found.shaRef(),
            Matchers.equalTo("84899952ccf723fe5e4306aac2c857f05ef4686a")
        );
    }

    /**
     * BitbucketCommit can return null if the commit is not found.
     */
    @Test
    public void getNullCommit() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                404,
                JsonValue.NULL
            )
        );
        final Commits commits = new BitbucketCommits(
            resources,
            URI.create("https://bitbucket.org/api/2.0/repositories"
                + "/crisketm/my-super-repo/commits"),
            Mockito.mock(Storage.class)
        );
        final Commit found = commits
            .getCommit("13f74048cd8b4c2fdafdf0d45771c1cf73b998de");
        MatcherAssert.assertThat(
            found,
            Matchers.nullValue()
        );
    }

    /**
     * BitbucketCommit throws an exception if getCommit received
     * 500 SERVER ERROR.
     */
    @Test(expected = IllegalStateException.class)
    public void getCommitThrowsException() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                500,
                JsonValue.NULL
            )
        );
        new BitbucketCommits(
            resources,
            URI.create("https://bitbucket.org/api/2.0/repositories"
                + "/crisketm/my-super-repo/commits"),
            Mockito.mock(Storage.class)
        ).getCommit("13f74048cd8b4c2fdafdf0d45771c1cf73b998de");
    }

    /**
     * BitbucketCommits can return the latest commit.
     */
    @Test
    public void getLatestCommit() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder()
                    .add("values", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add("hash",
                                "13f74048cd8b4c2fdafdf0d45771c1cf73b998de")
                            .add("author", Json.createObjectBuilder()
                                .add("account_id",
                                    "601e661dcd564b00686f4e4b")
                                .build())
                            .build())
                        .add(Json.createObjectBuilder()
                            .add("hash",
                                "84899952ccf723fe5e4306aac2c857f05ef4686a")
                            .add("author", Json.createObjectBuilder()
                                .add("account_id",
                                    "601e661dcd564b00686f4e4b")
                                .build())
                            .build())
                        .build()
                    )
                    .build()
            )
        );
        final Commits commits = new BitbucketCommits(
            resources,
            URI.create("https://bitbucket.org/api/2.0/repositories"
                + "/crisketm/my-super-repo/commits"),
            Mockito.mock(Storage.class)
        );
        final Commit latest = commits.latest();
        MatcherAssert.assertThat(
            latest.author(),
            Matchers.equalTo("601e661dcd564b00686f4e4b")
        );
        MatcherAssert.assertThat(
            latest.shaRef(),
            Matchers.equalTo("13f74048cd8b4c2fdafdf0d45771c1cf73b998de")
        );
    }

    /**
     * BitbucketCommits.latest() throws ISE if the HTTP response is not 200 OK.
     */
    @Test(expected = IllegalStateException.class)
    public void getLatestCommitThrowsIse() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                500,
                JsonValue.NULL
            )
        );
        new BitbucketCommits(
            resources,
            URI.create("https://bitbucket.org/api/2.0/repositories"
                + "/crisketm/my-super-repo/commits"),
            Mockito.mock(Storage.class)
        ).latest();
    }

}