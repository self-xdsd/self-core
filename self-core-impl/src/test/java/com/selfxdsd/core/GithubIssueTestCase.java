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
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GithubIssue}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class GithubIssueTestCase {

    /**
     * Github Issue can return its ID.
     */
    @Test
    public void returnsId() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder().add("number", 1).build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.issueId(), Matchers.equalTo("1"));
    }

    /**
     * Github Issue can return its provider.
     */
    @Test
    public void returnsProvider() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder().add("number", 1).build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.provider(), Matchers.equalTo("github"));
    }

    /**
     * Github Issue can return the DEV role when it is not a PR.
     */
    @Test
    public void returnsDevRole() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder()
                .add("number", 1)
                .add("html_url", "http://localhost/issues/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * Github Issue can return the REV role when it is a PR.
     */
    @Test
    public void returnsRevRole() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/pull/1"),
            Json.createObjectBuilder()
                .add("number", 1)
                .add("html_url", "http://localhost/pull/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.role(),
            Matchers.equalTo(Contract.Roles.REV)
        );
    }

    /**
     * GithubIssue can return the fullName of the Repo it belongs to,
     * from an Issue url.
     */
    @Test
    public void returnsRepoFullNameFromIssue() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder()
                .add("number", 1)
                .add(
                    "url",
                    "https://api.github.com/repos/"
                    + "amihaiemil/docker-java-api/issues/1"
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
    }

    /**
     * GithubIssue can return the fullName of the Repo it belongs to,
     * from a PR url.
     */
    @Test
    public void returnsRepoFullNameFromPullRequest() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder()
                .add("number", 1)
                .add(
                    "url",
                    "https://api.github.com/repos/"
                        + "amihaiemil/docker-java-api/pull/1"
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("amihaiemil/docker-java-api")
        );
    }

    /**
     * GithubIssue can return its author.
     */
    @Test
    public void returnsAuthor() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder()
                .add("id", 1)
                .add(
                    "user",
                    Json.createObjectBuilder()
                        .add("login", "amihaiemil")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.author(),
            Matchers.equalTo("amihaiemil")
        );
    }

    /**
     * GithubIssue can return its body.
     */
    @Test
    public void returnsBody() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder()
                .add("id", 1)
                .add(
                    "user",
                    Json.createObjectBuilder()
                        .add("login", "amihaiemil")
                        .build()
                ).add("body", "Issue description here.")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.body(),
            Matchers.equalTo("Issue description here.")
        );
    }

    /**
     * Returns Issue's comments.
     */
    @Test
    public void returnsComments(){
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder().build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.comments(),
            Matchers.instanceOf(DoNotRepeat.class)
        );
    }

    /**
     * A GithubIssue can return its Estimation.
     */
    @Test
    public void returnsEstimation() {
        final Estimation estimation = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder()
                .add("number", 1)
                .add("html_url", "http://localhost/issues/1")
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        ).estimation();
        MatcherAssert.assertThat(
            estimation,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(LabelsEstimation.class)
            )
        );
    }

    /**
     * A new user can be assigned ok (receives CREATED).
     */
    @Test
    public void assignsUserSuccessfully() {
        final MockJsonResources resources = new MockJsonResources(
            new AccessToken.Github("github123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().build()
            )
        );
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        final boolean res = issue.assign("george");
        MatcherAssert.assertThat(
            res, Matchers.is(Boolean.TRUE)
        );
        final MockJsonResources.MockRequest assign = resources
            .requests()
            .atIndex(0);

        MatcherAssert.assertThat(
            assign.getAccessToken().value(),
            Matchers.equalTo("token github123")
        );
        MatcherAssert.assertThat(
            assign.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            assign.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(
                        "assignees",
                        Json.createArrayBuilder()
                            .add("george")
                            .build()
                    ).build()
            )
        );
        MatcherAssert.assertThat(
            assign.getUri().toString(),
            Matchers.equalTo("http://localhost/issues/1/assignees")
        );
    }

    /**
     * We receive a NOT FOUND status when trying to assing a user to an Issue.
     */
    @Test
    public void assignsUserNotFound() {
        final MockJsonResources resources = new MockJsonResources(
            new AccessToken.Github("github123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                Json.createObjectBuilder().build()
            )
        );
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        final boolean res = issue.assign("george");
        MatcherAssert.assertThat(
            res, Matchers.is(Boolean.FALSE)
        );
        final MockJsonResources.MockRequest assign = resources
            .requests()
            .atIndex(0);

        MatcherAssert.assertThat(
            assign.getAccessToken().value(),
            Matchers.equalTo("token github123")
        );
        MatcherAssert.assertThat(
            assign.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            assign.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(
                        "assignees",
                        Json.createArrayBuilder()
                            .add("george")
                            .build()
                    ).build()
            )
        );
        MatcherAssert.assertThat(
            assign.getUri().toString(),
            Matchers.equalTo("http://localhost/issues/1/assignees")
        );
    }

    /**
     * A user can be unassigned (receives OK).
     */
    @Test
    public void unassignsUserOk() {
        final MockJsonResources resources = new MockJsonResources(
            new AccessToken.Github("github123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().build()
            )
        );
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        final boolean res = issue.unassign("george");
        MatcherAssert.assertThat(
            res, Matchers.is(Boolean.TRUE)
        );
        final MockJsonResources.MockRequest assign = resources
            .requests()
            .atIndex(0);

        MatcherAssert.assertThat(
            assign.getAccessToken().value(),
            Matchers.equalTo("token github123")
        );
        MatcherAssert.assertThat(
            assign.getMethod(),
            Matchers.equalTo("DELETE")
        );
        MatcherAssert.assertThat(
            assign.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(
                        "assignees",
                        Json.createArrayBuilder()
                            .add("george")
                            .build()
                    ).build()
            )
        );
        MatcherAssert.assertThat(
            assign.getUri().toString(),
            Matchers.equalTo("http://localhost/issues/1/assignees")
        );
    }

    /**
     * We receive NOT FOUND when trying to unassign a user.
     */
    @Test
    public void unassignsUserNotFound() {
        final MockJsonResources resources = new MockJsonResources(
            new AccessToken.Github("github123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                Json.createObjectBuilder().build()
            )
        );
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        final boolean res = issue.unassign("george");
        MatcherAssert.assertThat(
            res, Matchers.is(Boolean.FALSE)
        );
        final MockJsonResources.MockRequest assign = resources
            .requests()
            .atIndex(0);

        MatcherAssert.assertThat(
            assign.getAccessToken().value(),
            Matchers.equalTo("token github123")
        );
        MatcherAssert.assertThat(
            assign.getMethod(),
            Matchers.equalTo("DELETE")
        );
        MatcherAssert.assertThat(
            assign.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add(
                        "assignees",
                        Json.createArrayBuilder()
                            .add("george")
                            .build()
                    ).build()
            )
        );
        MatcherAssert.assertThat(
            assign.getUri().toString(),
            Matchers.equalTo("http://localhost/issues/1/assignees")
        );
    }

    /**
     * Issue.close() works if the received status is 200 OK.
     */
    @Test
    public void closesOk() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("PATCH")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("state", "closed")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("http://localhost/issues/1")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        issue.close();
    }

    /**
     * Issue.close() doesn't throw an exception if
     * the response status != 200 OK.
     */
    @Test
    public void closesNotFound() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("PATCH")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("state", "closed")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("http://localhost/issues/1")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        issue.close();
    }

    /**
     * Issue.reopen() works if the received status is 200 OK.
     */
    @Test
    public void reopensOk() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("PATCH")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("state", "open")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("http://localhost/issues/1")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        issue.reopen();
    }

    /**
     * Issue.reopen() doesn't throw an exception if
     * the response status != 200 OK.
     */
    @Test
    public void reopensNotFound() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Github("github123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("token github123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("PATCH")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("state", "open")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("http://localhost/issues/1")
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        issue.reopen();
    }

    /**
     * GithubIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedTrue() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder().add("state", "closed").build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * GithubIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedFalse() {
        final Issue issue = new GithubIssue(
            URI.create("http://localhost/issues/1"),
            Json.createObjectBuilder().add("state", "open").build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.FALSE)
        );
    }
}
