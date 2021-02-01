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

import com.selfxdsd.api.Comments;
import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Estimation;
import com.selfxdsd.api.Issue;
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
 * Unit tests for {@link GitlabIssue}.
 * @author criske
 * @version $Id$
 * @since 0.0.38
 */
public final class GitlabIssueTestCase {

    /**
     * Gitlab Issue can return its ID.
     */
    @Test
    public void returnsId() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("iid", 1).build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.issueId(), Matchers.equalTo("1"));
    }

    /**
     * Gitlab Issue can return its provider.
     */
    @Test
    public void returnsProvider() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("iid", 1).build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(issue.provider(), Matchers.equalTo("gitlab"));
    }

    /**
     * Gitlab Issue can return the DEV role when it is not a PR.
     */
    @Test
    public void returnsDevRole() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder()
                .add("iid", 1)
                .add("web_url", "http://gitlab.com/john/"
                    + "test/-/issues/1")
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
     * Gitlab Issue can return the REV role when it is a PR.
     */
    @Test
    public void returnsRevRole() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("iid", 1)
                .add("web_url", "http://gitlab.com/john/"
                    + "test/-/merge_requests/1")
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
     * GitlabIssue can return the fullName of the Repo it belongs to,
     * from an Issue reference object.
     */
    @Test
    public void returnsRepoFullNameFromIssueReference() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder()
                .add("references",
                    Json.createObjectBuilder()
                        .add("full", "john/test#1")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("john/test")
        );
    }

    /**
     * GitlabIssue can return the fullName of the Repo it belongs to,
     * from an Merge Request reference object.
     */
    @Test
    public void returnsRepoFullNameFromMergeRequestReference() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("references",
                    Json.createObjectBuilder()
                        .add("full", "john/test!1")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("john/test")
        );
    }

    /**
     * GitlabIssue can return its author.
     */
    @Test
    public void returnsAuthor() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("author",
                    Json.createObjectBuilder()
                        .add("username", "amihaiemil")
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
     * GitlabIssue can return its assignee.
     */
    @Test
    public void returnsAssigneeUsername() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("assignee",
                    Json.createObjectBuilder()
                        .add("username", "amihaiemil")
                        .build()
                )
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.assignee(),
            Matchers.equalTo("amihaiemil")
        );
    }

    /**
     * GitlabIssue can return no assignee.
     */
    @Test
    public void returnsNoAssignee() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.assignee(),
            Matchers.nullValue()
        );
    }

    /**
     * GitlabIssue can return its body.
     */
    @Test
    public void returnsBody() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            Json.createObjectBuilder()
                .add("description", "Issue description here.")
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
     *GitlabIssue.comments() has comments.
     */
    @Test
    public void hasComments() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(200, JsonValue
                .EMPTY_JSON_ARRAY)
        );
        final Comments comments = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        ).comments();

        comments.iterator();

        final MockJsonResources.MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(req.getUri(), Matchers
            .equalTo(URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1/notes")));
        MatcherAssert.assertThat(comments, Matchers
            .instanceOf(DoNotRepeat.class));
    }

    /**
     * A GitlabIssue can return its Estimation.
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
     *GitlabIssue.assign(...) is successful.
     */
    @Test
    public void assignIsSuccessful() {
        final MockJsonResources resources = new MockJsonResources((req) -> {
            final JsonValue body;
            if (req.getUri().toString()
                .endsWith("search?scope=users&search=john")) {
                body = Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("username", "john")
                        .build())
                    .build();
            } else {
                body = JsonValue.NULL;
            }
            return new MockJsonResources.MockResource(200, body);
        });

        boolean assigned = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        ).assign("john");

        final MockJsonResources.MockRequests requests = resources.requests();
        //checking GET "members" request
        MatcherAssert.assertThat(requests.first().getUri().toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/search?scope=users&search=john"));
        MatcherAssert.assertThat(requests.first().getMethod(), Matchers
            .equalTo("GET"));
        //checking PUT "assign" request
        MatcherAssert.assertThat(requests.atIndex(1).getUri().toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"));
        MatcherAssert.assertThat(requests.atIndex(1).getMethod(), Matchers
            .equalTo("PUT"));
        MatcherAssert.assertThat(requests.atIndex(1).getBody(), Matchers
            .equalTo(Json.createObjectBuilder()
                .add("assignee_id", 1)
                .build()));

        MatcherAssert.assertThat(assigned, Matchers.is(true));
    }

    /**
     *GitlabIssue.assign(...) is successful when issue is a merge request.
     */
    @Test
    public void assignIsSuccessfulWhenIssueIsMergeRequest() {
        final MockJsonResources resources = new MockJsonResources((req) -> {
            final JsonValue body;
            if (req.getUri().toString()
                .endsWith("search?scope=users&search=john")) {
                body = Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("username", "john")
                        .build())
                    .build();
            } else {
                body = JsonValue.NULL;
            }
            return new MockJsonResources.MockResource(200, body);
        });

        boolean assigned = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        ).assign("john");

        final MockJsonResources.MockRequests requests = resources.requests();
        //checking GET "members" request
        MatcherAssert.assertThat(requests.first().getUri().toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/search?scope=users&search=john"));
        MatcherAssert.assertThat(requests.first().getMethod(), Matchers
            .equalTo("GET"));
        //checking PUT "assign" request
        MatcherAssert.assertThat(requests.atIndex(1).getUri().toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/merge_requests/1"));
        MatcherAssert.assertThat(requests.atIndex(1).getMethod(), Matchers
            .equalTo("PUT"));
        MatcherAssert.assertThat(requests.atIndex(1).getBody(), Matchers
            .equalTo(Json.createObjectBuilder()
                .add("assignee_id", 1)
                .build()));

        MatcherAssert.assertThat(assigned, Matchers.is(true));
    }

    /**
     *GitlabIssue.assign(...) fails if assign response is not OK.
     */
    @Test
    public void assignFailsIfAssignResponseIsNotOk() {
        final MockJsonResources resources = new MockJsonResources((req) -> {
            final MockJsonResources.MockResource res;
            if (req.getUri().toString()
                .endsWith("search?scope=users&search=john")) {
                res = new MockJsonResources.MockResource(200, Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("username", "john")
                        .build())
                    .build());
            } else {
                res = new MockJsonResources.MockResource(500,
                    JsonValue.NULL);
            }
            return res;
        });
        boolean assigned = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        ).assign("john");

        MatcherAssert.assertThat(assigned, Matchers.is(false));
    }

    /**
     *GitlabIssue.assign(...) fails if getting members response is not OK.
     */
    @Test
    public void assignFailsIfMembersResponseIsNotOk() {
        final MockJsonResources resources = new MockJsonResources((req) -> {
            final int code;
            if (req.getUri().toString()
                .endsWith("search?scope=users&search=john")) {
                code = 500;
            } else {
                code = 200;
            }
            return new MockJsonResources.MockResource(code, JsonValue.NULL);
        });
        boolean assigned = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        ).assign("john");

        MatcherAssert.assertThat(assigned, Matchers.is(false));
    }

    /**
     *GitlabIssue.assign(...) if user id is not found.
     */
    @Test
    public void assignFailsIfUserIdIsNotFound() {
        final MockJsonResources resources = new MockJsonResources((req) -> {
            final JsonValue body;
            if (req.getUri().toString()
                .endsWith("search?scope=users&search=john")) {
                body = Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("username", "john")
                        .build())
                    .build();
            } else {
                body = JsonValue.NULL;
            }
            return new MockJsonResources.MockResource(200, body);
        });
        boolean assigned = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        ).assign("dan");

        MatcherAssert.assertThat(assigned, Matchers.is(false));
    }

    /**
     * GitlabIssue.close() sends the right request.
     */
    @Test
    public void closeSendsRightRequest() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().build()
            )
        );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects"
            + "/john%2Ftest/issues/1"
        );
        final Issue issue = new GitlabIssue(
            uri,
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        issue.close();
        final MockJsonResources.MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getUri(), Matchers.equalTo(uri)
        );
        MatcherAssert.assertThat(
            req.getMethod(), Matchers.equalTo("PUT")
        );
        MatcherAssert.assertThat(
            req.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("state_event", "close")
                    .build()
            )
        );
    }

    /**
     * GitlabIssue.reopen() sends the right request.
     */
    @Test
    public void reopenSendsRightRequest() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().build()
            )
        );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects"
            + "/john%2Ftest/issues/1"
        );
        final Issue issue = new GitlabIssue(
            uri,
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        issue.reopen();
        final MockJsonResources.MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getUri(), Matchers.equalTo(uri)
        );
        MatcherAssert.assertThat(
            req.getMethod(), Matchers.equalTo("PUT")
        );
        MatcherAssert.assertThat(
            req.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("state_event", "reopen")
                    .build()
            )
        );
    }

    /**
     * GitlabIssue.unassign(...) sends the right request.
     */
    @Test
    public void unassignSendsRightRequest() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().build()
            )
        );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects"
            + "/john%2Ftest/issues/1"
        );
        final Issue issue = new GitlabIssue(
            uri,
            JsonObject.EMPTY_JSON_OBJECT,
            Mockito.mock(Storage.class),
            resources
        );
        issue.unassign("amihaiemil");
        final MockJsonResources.MockRequest req = resources.requests().first();
        MatcherAssert.assertThat(
            req.getUri(), Matchers.equalTo(uri)
        );
        MatcherAssert.assertThat(
            req.getMethod(), Matchers.equalTo("PUT")
        );
        MatcherAssert.assertThat(
            req.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("assignee_ids", "")
                    .build()
            )
        );
    }

    /**
     * GitlabIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedTrueWhenClosed() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
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
     * GitlabIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedTrueWhenMerged() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("state", "merged").build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * GitlabIssue can return its state flag.
     */
    @Test
    public void returnsIsClosedFalse() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().add("state", "open").build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * GitlabIssue can return its wrapped json object.
     */
    @Test
    public void returnsItsJson(){
        final JsonObject json = JsonObject.EMPTY_JSON_OBJECT;
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            json,
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.json(),
            Matchers.equalTo(json)
        );
    }

    /**
     * GitlabIssue can return its Labels.
     */
    @Test
    public void returnsLabels() {
        final Issue issue = new GitlabIssue(
            URI.create("https://gitlab.com/api/v4/projects"
                + "/john%2Ftest/issues/1"),
            Json.createObjectBuilder().build(),
            Mockito.mock(Storage.class),
            Mockito.mock(JsonResources.class)
        );
        MatcherAssert.assertThat(
            issue.labels(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(GitlabIssueLabels.class)
            )
        );
    }
}
