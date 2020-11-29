/**
 * Copyright (c) 2020, Self XDSD Contributors
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

import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link GithubIssues}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.9
 */
public final class GithubIssuesTestCase {

    /**
     * GithubIssue can receive an issue in JSON format
     * and return it as Issue.
     * @checkstyle LineLength (10 lines)
     */
    @Test
    public void receivesIssueFromJson(){
        final Issues issues = new GithubIssues(
            new JsonResources.JdkHttp(),
            URI.create(
                "https://api.github.com/repos/amihaiemil/docker-java-api/issues"
            ),
            mock(Repo.class),
            mock(Storage.class)
        );
        final JsonObject json = Json.createObjectBuilder()
            .add("number", 3)
            .build();
        final Issue issue = issues.received(json);
        MatcherAssert.assertThat(issue.issueId(), Matchers.equalTo("3"));
        MatcherAssert.assertThat(issue.json(), Matchers.equalTo(json));
    }

    /**
     * GithubIssues.open(...) works if the received response is 201 CREATED.
     */
    @Test
    public void opensIssueCreated() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("title", "Issue for test")
                                .add("body", "Body of the test Issue...")
                                .add("labels", Json.createArrayBuilder())
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/repos/amihaiemil/repo"
                                + "/issues"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        Json.createObjectBuilder().add("number", 123).build()
                    );
                }
            )
        );
        final Issue created = provider
            .repo("amihaiemil", "repo")
            .issues()
            .open("Issue for test", "Body of the test Issue...");
        MatcherAssert.assertThat(
            created, Matchers.notNullValue()
        );
        MatcherAssert.assertThat(
            created.issueId(), Matchers.equalTo("123")
        );
    }

    /**
     * GithubIssues.open(...) works if the received response is 200 OK.
     */
    @Test
    public void opensIssueOk() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final MockJsonResources resources = new MockJsonResources(
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder().add("number", 123).build()
            )
        );
        final Provider provider = new Github(
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
                "https://api.github.com/repos/amihaiemil/repo"
                + "/labels"
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
                "https://api.github.com/repos/amihaiemil/repo"
                    + "/labels"
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
                    .add("body", "Body of the test Issue...")
                    .add(
                        "labels",
                        Json.createArrayBuilder()
                            .add("bug")
                            .add("puzzle")
                    ).build()
            )
        );
        MatcherAssert.assertThat(
            open.getUri().toString(),
            Matchers.equalTo(
                "https://api.github.com/repos/amihaiemil/repo"
                + "/issues"
            )
        );
    }

    /**
     * GithubIssues.open(...) should throw an ISE if the received status
     * is not 201 CREATED or 200 OK.
     */
    @Test(expected = IllegalStateException.class)
    public void openIssueComplainsOnNotFound() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(
                            Json.createObjectBuilder()
                                .add("title", "Issue for test")
                                .add("body", "Body of the test Issue...")
                                .add("labels", Json.createArrayBuilder())
                                .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/repos/amihaiemil/repo"
                                + "/issues"
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
     * GithubIssues.search(...) works if the received response is 200 OK.
     */
    @Test
    public void searchesIssuesOk() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonObject.NULL)
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/search/issues?"
                            + "q=test+repo:amihaiemil/repo+label:puzzle"
                            + "&sort=created&order=desc&per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json
                            .createObjectBuilder()
                            .add(
                                "items",
                                Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add("number", 123)
                                    ).add(
                                        Json.createObjectBuilder()
                                            .add("number", 124)
                                    ).add(
                                        Json.createObjectBuilder()
                                            .add("number", 125)
                                    )
                            )
                            .build()
                    );
                }
            )
        );
        final Issues found = provider
            .repo("amihaiemil", "repo")
            .issues()
            .search("test", "puzzle");
        MatcherAssert.assertThat(
            found, Matchers.iterableWithSize(3)
        );
    }

    /**
     * GithubIssues.search(...) works with no text
     * and the received response is 200 OK.
     */
    @Test
    public void searchesIssuesOkNoText() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonObject.NULL)
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/search/issues?"
                            + "q=repo:amihaiemil/repo+label:puzzle"
                            + "&sort=created&order=desc&per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json
                            .createObjectBuilder()
                            .add(
                                "items",
                                Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add("number", 123)
                                    ).add(
                                    Json.createObjectBuilder()
                                        .add("number", 124)
                                ).add(
                                    Json.createObjectBuilder()
                                        .add("number", 125)
                                )
                            )
                            .build()
                    );
                }
            )
        );
        final Issues found = provider
            .repo("amihaiemil", "repo")
            .issues()
            .search("", "puzzle");
        MatcherAssert.assertThat(
            found, Matchers.iterableWithSize(3)
        );
    }

    /**
     * GithubIssues.search(...) works with no text and no labels
     * and the received response is 200 OK.
     */
    @Test
    public void searchesIssuesOkNoTextNoLabels() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonObject.NULL)
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/search/issues?"
                            + "q=repo:amihaiemil/repo"
                            + "&sort=created&order=desc&per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json
                            .createObjectBuilder()
                            .add(
                                "items",
                                Json.createArrayBuilder()
                                    .add(
                                        Json.createObjectBuilder()
                                            .add("number", 123)
                                    ).add(
                                    Json.createObjectBuilder()
                                        .add("number", 124)
                                ).add(
                                    Json.createObjectBuilder()
                                        .add("number", 125)
                                )
                            )
                            .build()
                    );
                }
            )
        );
        final Issues found = provider
            .repo("amihaiemil", "repo")
            .issues()
            .search("");
        MatcherAssert.assertThat(
            found, Matchers.iterableWithSize(3)
        );
    }

    /**
     * GithubIssues.search(...) works if the received response is 200 OK
     * with no results.
     */
    @Test
    public void searchesIssuesOkNoResults() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonObject.NULL)
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/search/issues?"
                                + "q=test+repo:amihaiemil/repo+label:puzzle"
                                + "&sort=created&order=desc&per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json
                            .createObjectBuilder()
                            .add(
                                "items",
                                Json.createArrayBuilder()
                            )
                            .build()
                    );
                }
            )
        );
        final Issues found = provider
            .repo("amihaiemil", "repo")
            .issues()
            .search("test", "puzzle");
        MatcherAssert.assertThat(
            found, Matchers.emptyIterable()
        );
    }

    /**
     * GithubIssues.search(...) works if the received response is NOT 200 OK.
     * It should return an empty iterable of Issues.
     */
    @Test
    public void searchesIssuesNotOk() {
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Provider provider = new Github(
            user,
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonObject.NULL)
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/search/issues?"
                                + "q=test+repo:amihaiemil/repo+label:puzzle"
                                + "&sort=created&order=desc&per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        final Issues found = provider
            .repo("amihaiemil", "repo")
            .issues()
            .search("test", "puzzle");
        MatcherAssert.assertThat(
            found, Matchers.emptyIterable()
        );
    }
}
