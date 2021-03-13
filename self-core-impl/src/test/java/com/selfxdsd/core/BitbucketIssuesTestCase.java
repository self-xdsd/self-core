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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
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
 * Unit tests for {@link BitbucketIssues}.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.69
 */
public final class BitbucketIssuesTestCase {

    /**
     * Can get an issue by its id.
     */
    @Test
    public void getsIssueById() {
        final MockJsonResources resources = new MockJsonResources(
            req -> {
                MatcherAssert.assertThat(
                    req.getMethod(),
                    Matchers.is("GET")
                );
                MatcherAssert.assertThat(
                    req.getUri().toString(),
                    Matchers.is(
                        "https://bitbucket.org/api/2.0/repositories/"
                            +"self/test/issues/1"
                    )
                );
                return new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder()
                        .add("id", 1)
                        .build()
                );
            }
        );

        final Issue issue = new BitbucketRepo(
            resources,
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test"
            ),
            Mockito.mock(User.class),
            Mockito.mock(Storage.class)
        ).issues().getById("1");

        MatcherAssert.assertThat(
            issue,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(BitbucketIssue.class)
        ));
        MatcherAssert.assertThat(issue.issueId(), Matchers.is("1"));
    }

    /**
     * BitbucketIssues::getById() returns null when issue not found.
     */
    @Test
    public void getIssueByIdIsNotFound() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                JsonValue.NULL
            )
        );
        final Issue issue= new BitbucketIssues(
            resources,
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");

        MatcherAssert.assertThat(issue, Matchers.nullValue());
    }

    /**
     * BitbucketIssues::getById() returns null when response code is 410.
     * (not available)
     */
    @Test
    public void getsIssueByIdNoContent() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_GONE,
                JsonValue.NULL
            )
        );
        final Issue issue= new BitbucketIssues(
            resources,
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");

        MatcherAssert.assertThat(issue, Matchers.nullValue());
    }

    /**
     * BitbucketIssues.getById() throws IllegalStateException when response
     * code is other then HTTP_OK, HTTP_NOT_FOUND or HTTP_GONE.
     */
    @Test(expected = IllegalStateException.class)
    public void getIssueByIdThrowsWhenResponseCodeNotProcessed() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_BAD_REQUEST,
                JsonValue.NULL
            )
        );
        new BitbucketIssues(
            resources,
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");
    }

    /**
     * Bitbucket.received(json) can create an Issue
     * received from a Provider as JsonObject.
     */
    @Test
    public void canCreateIssueFromJson() {
        final Issues issues = new BitbucketIssues(
            Mockito.mock(JsonResources.class),
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        );
        final JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .build();
        final Issue issue = issues.received(json);
        MatcherAssert.assertThat(
            issue,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(BitbucketIssue.class)
            )
        );
        MatcherAssert.assertThat(
            issue.issueId(),
            Matchers.equalTo("1")
        );
        MatcherAssert.assertThat(
            issue.json(),
            Matchers.equalTo(json)
        );
    }

    /**
     * Iterating over all issues is not allowed.
     */
    @Test(expected = IllegalStateException.class)
    public void iterationOverAllIssuesNotAllowed() {
        new BitbucketIssues(
            Mockito.mock(JsonResources.class),
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).iterator();
    }

    /**
     * Can open an issue.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void opensAnIssue() {
        new BitbucketIssues(
            Mockito.mock(JsonResources.class),
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).open("test", "annoying");
    }

    /**
     * Can search an issue by title and labels.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void searchesIssues(){
        new BitbucketIssues(
            Mockito.mock(JsonResources.class),
            URI.create(
                "https://bitbucket.org/api/2.0/repositories/self/test/issues"
            ),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).search("test");
    }

}
