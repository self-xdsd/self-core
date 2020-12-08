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
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GitlabWebhooks}.
 * @author criske
 * @version $Id$
 * @since 0.0.13
 */
public final class GitlabWebhooksTestCase {

    /**
     * A new Webhook can be added ok (receives CREATED).
     */
    @Test
    public void addsWebhookCreated() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Gitlab(
            Mockito.mock(User.class),
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
                        Matchers.equalTo(Json.createObjectBuilder()
                            .add("id", "amihaiemil%2Frepo")
                            .add("url", "null/gitlab/"
                                + "amihaiemil/repo")
                            .add("issues_events", true)
                            .add("token", "webhook_tok333n")
                            .build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://gitlab.com/api/v4/projects"
                                + "/amihaiemil%2Frepo/hooks"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        final boolean res = provider
            .repo("amihaiemil", "repo")
            .webhooks()
            .add(project);
        MatcherAssert.assertThat(res, Matchers.is(Boolean.TRUE));
    }

    /**
     * A new Webhook is added, but receives status NOT FOUND.
     */
    @Test
    public void addsWebhookNotFound() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Gitlab("github123"),
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    JsonValue.NULL
                )
            )
        );
        final boolean res = provider
            .repo("amihaiemil", "repo")
            .webhooks()
            .add(project);
        MatcherAssert.assertThat(res, Matchers.is(Boolean.FALSE));
    }

    /**
     * GitlabWebhooks can be iterated.
     */
    @Test
    public void iteratesWebhooksOk() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Gitlab(
            Mockito.mock(User.class),
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://gitlab.com/api/v4/projects/"
                            + "amihaiemil%2Frepo/hooks?per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createArrayBuilder()
                            .add(Json.createObjectBuilder())
                            .add(Json.createObjectBuilder())
                            .build()
                    );
                }
            )
        );
        MatcherAssert.assertThat(
            provider
                .repo("amihaiemil", "repo")
                .webhooks(),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * GitlabWebhooks is empty iterable due to 404 NOT FOUND.
     */
    @Test
    public void iteratesWebhooksNotFound() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Gitlab(
            Mockito.mock(User.class),
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
                        Matchers.equalTo("GET")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://gitlab.com/api/v4/projects/"
                            + "amihaiemil%2Frepo/hooks?per_page=100"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        MatcherAssert.assertThat(
            provider
                .repo("amihaiemil", "repo")
                .webhooks(),
            Matchers.iterableWithSize(0)
        );
    }

    /**
     * GitlabWebhooks doesn't remove anything if there is
     * no self-xdsd webhook present.
     */
    @Test
    public void doesNotRemoveNonSelfHooks() {
        final MockJsonResources resources =
            new MockJsonResources(req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add("id", 123)
                            .add("url", "https://example.com").build()
                    ).add(
                    Json.createObjectBuilder()
                        .add("id", 456)
                        .add("url", "https://example2.com")
                        .build()
                ).build()
            )
            );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "amihaiemil%2Frepo/hooks"
        );

        boolean removed = new GitlabWebhooks(
            resources, uri, Mockito.mock(Storage.class)
        ).remove();

        MatcherAssert.assertThat(
            removed,
            Matchers.is(Boolean.TRUE)
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(0).getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(0).getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "?per_page=100")
            )
        );
        MatcherAssert.assertThat(
            resources.requests(),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * GitlabWebhooks can remove all webhooks related to Self XDSD.
     */
    @Test
    public void removesSelfWebhooks() {
        final MockJsonResources resources =
            new MockJsonResources(req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createArrayBuilder()
                    .add(
                        Json.createObjectBuilder()
                            .add("id", 123)
                            .add("url", "https://self-xdsd.com")
                            .build()
                    ).add(
                    Json.createObjectBuilder()
                        .add("id", 789)
                        .add("url", "https://non-self.com")
                        .build()
                    ).add(
                        Json.createObjectBuilder()
                            .add("id", 456)
                            .add("url", "https://self-xdsd.go.ro")
                            .build()
                    ).build()
            )
            );
        final URI uri = URI.create(
            "https://gitlab.com/api/v4/projects/"
            + "amihaiemil%2Frepo/hooks"
        );

        new GitlabWebhooks(
            resources, uri, Mockito.mock(Storage.class)
        ).remove();

        MatcherAssert.assertThat(
            resources.requests().atIndex(0).getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(0).getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "?per_page=100")
            )
        );
        MatcherAssert.assertThat(
            resources.requests(),
            Matchers.iterableWithSize(3)
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(1).getMethod(),
            Matchers.equalTo("DELETE")
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(1).getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "/123")
            )
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(2).getMethod(),
            Matchers.equalTo("DELETE")
        );
        MatcherAssert.assertThat(
            resources.requests().atIndex(2).getUri(),
            Matchers.equalTo(
                URI.create(uri.toString() + "/456")
            )
        );
    }
}
