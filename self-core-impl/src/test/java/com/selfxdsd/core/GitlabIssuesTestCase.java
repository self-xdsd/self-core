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
 * Unit tests for {@link GitlabIssues}.
 * @author criske
 * @version $Id$
 * @since 0.0.38
 */
public final class GitlabIssuesTestCase {


    /**
     * GitlabIssues.getById(...) can get an issue by its id.
     */
    @Test
    public void getsIssueById() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder()
                    .add("iid", 1)
                    .build()
            )
        );
        final Issue issue= new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");

        final MockJsonResources.MockRequest req = resources.requests().first();

        MatcherAssert.assertThat(req.getUri().toString(), Matchers
            .equalTo("https://gitlab.com/api/v4/projects/john%2Ftest/issues/1")
        );
        MatcherAssert.assertThat(req.getMethod(), Matchers
            .equalTo("GET")
        );
        MatcherAssert.assertThat(issue, Matchers.allOf(
            Matchers.notNullValue(),
            Matchers.instanceOf(WithContributorLabel.class)
        ));
        MatcherAssert.assertThat(issue.issueId(), Matchers.is("1"));
    }

    /**
     * GitlabIssues.getById(...) can't get an issue by its id.
     */
    @Test
    public void getIssueByIdIsNotFound() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NOT_FOUND,
                JsonValue.NULL
            )
        );
        final Issue issue= new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");

        MatcherAssert.assertThat(issue, Matchers.nullValue());
    }

    /**
     * GitlabIssues.getById(...) returns null when response code is
     * no content.
     */
    @Test
    public void getsIssueByIdNoContent() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_NO_CONTENT,
                JsonValue.NULL
            )
        );
        final Issue issue= new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");

        MatcherAssert.assertThat(issue, Matchers.nullValue());
    }

    /**
     * GitlabIssues.getById(...) throws IllegalStateException when response
     * code is other then HTTP_OK, HTTP_NOT_FOUND or HTTP_NO_CONTENT.
     */
    @Test(expected = IllegalStateException.class)
    public void getIssueByIdThrowsWhenResponseCodeNotProcessed() {
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_UNAVAILABLE,
                JsonValue.NULL
            )
        );
        new GitlabIssues(
            resources,
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).getById("1");
    }

    /**
     * GitlabIssues.received(json) can create an Issue
     * received from a Provider as JsonObject.
     */
    @Test
    public void canCreateIssueFromJson() {
        final Issues issues = new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        );
        final JsonObject json = Json.createObjectBuilder()
            .add("iid", 1)
            .build();
        final Issue issue = issues.received(json);
        MatcherAssert.assertThat(
            issue,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(WithContributorLabel.class)
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
     * Iterating over all Gitlab issues is not allowed.
     */
    @Test(expected = IllegalStateException.class)
    public void iterationOverAllIssuesNotAllowed() {
        new GitlabIssues(
            Mockito.mock(JsonResources.class),
            URI.create("https://gitlab.com/api/v4/projects/john%2Ftest/issues"),
            Mockito.mock(Repo.class),
            Mockito.mock(Storage.class)
        ).iterator();
    }

    /**
     * GitlabIssues.open(...) works if the received response is 201 CREATED
     * and there are no labels added.
     */
    @Test
    public void opensIssueWithoutLabels() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Gitlab("gitlab123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("gitlab123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("title", "Issue for test")
                                .add("description", "Body of the Issue...")
                                .add("labels", "")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://gitlab.com/api/v4/projects/"
                            + "amihaiemil%2Frepo/issues"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        Json.createObjectBuilder().add("iid", 123).build()
                    );
                }
            )
        );
        final Issue created = provider
            .repo("amihaiemil", "repo")
            .issues()
            .open("Issue for test", "Body of the Issue...");
        MatcherAssert.assertThat(
            created, Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            created.issueId(), Matchers.equalTo("123")
        );
    }

    /**
     * GitlabIssues.open(...) works if the received response is 201 CREATED.
     */
    @Test
    public void opensIssueWithLabels() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().add("iid", 123).build()
            )
        );
        final Provider provider = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            resources
        );
        final Issue created = provider
            .repo("amihaiemil", "repo")
            .issues()
            .open(
                "Issue for test",
                "Body of the test Issue...",
                "bug", "puzzle"
            );
        MatcherAssert.assertThat(
            created, Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            created.issueId(), Matchers.equalTo("123")
        );

        final MockJsonResources.MockRequest bug = resources.requests()
            .atIndex(0);
        MatcherAssert.assertThat(
            bug.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            bug.getBody().asJsonObject().getString("name"),
            Matchers.equalTo("bug")
        );
        MatcherAssert.assertThat(
            bug.getUri().toString(),
            Matchers.equalTo(
                "https://gitlab.com/api/v4/projects/amihaiemil%2Frepo/labels"
            )
        );

        final MockJsonResources.MockRequest puzzle = resources.requests()
            .atIndex(1);
        MatcherAssert.assertThat(
            puzzle.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            puzzle.getBody().asJsonObject().getString("name"),
            Matchers.equalTo("puzzle")
        );
        MatcherAssert.assertThat(
            puzzle.getUri().toString(),
            Matchers.equalTo(
                "https://gitlab.com/api/v4/projects/amihaiemil%2Frepo/labels"
            )
        );

        final MockJsonResources.MockRequest open = resources.requests()
            .atIndex(2);
        MatcherAssert.assertThat(
            open.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            open.getBody(),
            Matchers.equalTo(
                Json.createObjectBuilder()
                    .add("title", "Issue for test")
                    .add("description", "Body of the test Issue...")
                    .add(
                        "labels",
                        "bug,puzzle"
                    ).build()
            )
        );
        MatcherAssert.assertThat(
            open.getUri().toString(),
            Matchers.equalTo(
                "https://gitlab.com/api/v4/projects/amihaiemil%2Frepo/issues"
            )
        );
    }

    /**
     * GitlabIssues.open(...) should throw an ISE if the received status
     * is not 201 CREATED or 200 OK.
     */
    @Test(expected = IllegalStateException.class)
    public void openIssueComplainsOnNotFound() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Gitlab("gitlab123"),
                req -> {
                    MatcherAssert.assertThat(
                        req.getAccessToken().value(),
                        Matchers.equalTo("gitlab123")
                    );
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("title", "Issue for test")
                                .add(
                                    "description",
                                    "Body of the test Issue..."
                                )
                                .add("labels", "")
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://gitlab.com/api/v4/projects/"
                            + "amihaiemil%2Frepo/issues"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        provider
            .repo("amihaiemil", "repo")
            .issues()
            .open("Issue for test", "Body of the test Issue...");
    }

    /**
     * GitlabIssues.search(...) can search a issues by title and labels.
     */
    @Test
    public void searchesIssuesByTitleAndLabels(){
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                            .add("iid", 1)
                            .build()
                    ).build()
            )
        );
        final Issues found = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            resources
        ).repo("amihaiemil", "repo")
            .issues()
            .search("hello world", "help wanted", "todo");

        MatcherAssert.assertThat(found, Matchers.allOf(
            Matchers.iterableWithSize(1),
            Matchers.instanceOf(FoundIssues.class)
        ));
        MatcherAssert.assertThat(found.iterator().next().issueId(),
            Matchers.equalTo("1"));
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getUri()
                .toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects/"
                + "amihaiemil%2Frepo/issues/?per_page=100"
                + "&search=hello+world"
                + "&labels=help+wanted,todo")
        );
    }

    /**
     * GitlabIssues.search(...) can search a issues by title text.
     */
    @Test
    public void searchesIssuesByTitle() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("iid", 1)
                        .build()
                    ).build()
            )
        );
        final Issues found = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            resources
        ).repo("amihaiemil", "repo")
            .issues()
            .search("hello world");

        MatcherAssert.assertThat(found, Matchers.allOf(
            Matchers.iterableWithSize(1),
            Matchers.instanceOf(FoundIssues.class)
        ));
        MatcherAssert.assertThat(found.iterator().next().issueId(),
            Matchers.equalTo("1"));
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getUri()
                .toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects/"
                + "amihaiemil%2Frepo/issues/?per_page=100"
                + "&search=hello+world")
        );
    }

    /**
     * GitlabIssues.search(...) can search a issues by labels.
     */
    @Test
    public void searchesIssuesByLabels(){
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("iid", 1)
                        .build()
                    ).build()
            )
        );
        final Issues found = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            resources
        ).repo("amihaiemil", "repo")
            .issues()
            .search(null, "help wanted", "todo");

        MatcherAssert.assertThat(found, Matchers.allOf(
            Matchers.iterableWithSize(1),
            Matchers.instanceOf(FoundIssues.class)
        ));
        MatcherAssert.assertThat(found.iterator().next().issueId(),
            Matchers.equalTo("1"));
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getUri()
                .toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects/"
                + "amihaiemil%2Frepo/issues/?per_page=100"
                + "&labels=help+wanted,todo")
        );
    }

    /**
     * GitlabIssues.search(...) can search a issues by labels.
     */
    @Test
    public void searchesIssuesAllWhenTitleAndLabelsMissing(){
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("iid", 1)
                        .build()
                    ).build()
            )
        );
        final Issues found = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            resources
        ).repo("amihaiemil", "repo")
            .issues()
            .search(null);

        MatcherAssert.assertThat(found, Matchers.allOf(
            Matchers.iterableWithSize(1),
            Matchers.instanceOf(FoundIssues.class)
        ));
        MatcherAssert.assertThat(found.iterator().next().issueId(),
            Matchers.equalTo("1"));
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(resources
                .requests()
                .first()
                .getUri()
                .toString(),
            Matchers.equalTo("https://gitlab.com/api/v4/projects/"
                + "amihaiemil%2Frepo/issues/?per_page=100")
        );
    }

    /**
     * GitlabIssues.search(...) returns empty issues if result code is other
     * than HTTP_OK.
     */
    @Test
    public void searchIssuesReturnsError(){
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_UNAVAILABLE,
                JsonValue.NULL
            )
        );
        final Issues found = new Gitlab(
            user,
            Mockito.mock(Storage.class),
            resources
        ).repo("amihaiemil", "repo")
            .issues()
            .search(null);
        MatcherAssert.assertThat(found, Matchers.allOf(
            Matchers.emptyIterable(),
            Matchers.instanceOf(FoundIssues.class)
        ));
    }
}
