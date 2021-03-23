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
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link BitbucketWebhooks}.
 * @author criske
 * @version $Id$
 * @since 0.0.72
 */
public final class BitbucketWebhooksTestCase {

    /**
     * A new Webhook can be added ok (receives CREATED).
     */
    @Test
    public void addsWebhookCreated() {
        final Project project = Mockito.mock(Project.class);
        final MockJsonResources res = new MockJsonResources(
            new AccessToken.Bitbucket("bitbucket123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_CREATED,
                Json.createObjectBuilder().build()
            )
        );
        final Provider provider = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final boolean added = provider
            .repo("amihaiemil", "repo")
            .webhooks()
            .add(project);

        final MockJsonResources.MockRequest req = res.requests().first();
        MatcherAssert.assertThat(
            req.getAccessToken().value(),
            Matchers.equalTo("Bearer bitbucket123")
        );
        MatcherAssert.assertThat(
            req.getMethod(),
            Matchers.equalTo("POST")
        );
        MatcherAssert.assertThat(
            req.getBody(),
            Matchers.equalTo(Json.createObjectBuilder()
                .add("description", "Self-XDSD PM")
                .add("url", "null/bitbucket/"
                    + "amihaiemil/repo")
                .add("active", true)
                .add("events",
                    Json.createArrayBuilder()
                        .add("repo:push")
                        .add("issue:created")
                        .add("issue:comment_created")
                        .add("pullrequest:created")
                        .add("pullrequest:comment_created")
                )
                .build()
            )
        );
        MatcherAssert.assertThat(
            req.getUri().toString(),
            Matchers.equalTo(
                "https://bitbucket.org/api/2.0"
                    + "/repositories/amihaiemil/repo/hooks"
            )
        );
        MatcherAssert.assertThat(added, Matchers.is(Boolean.TRUE));
    }

    /**
     * A new Webhook is added, but receives status NOT FOUND.
     */
    @Test
    public void addsWebhookNotFound() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Bitbucket("bitbucket123"),
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
     * BitbucketWebhooks can be iterated.
     */
    @Test
    public void iteratesWebhooksOk() {
        final Project project = Mockito.mock(Project.class);
        final MockJsonResources res = new MockJsonResources(
            new AccessToken.Bitbucket("bitbucket123"),
            req -> new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_OK,
                Json.createObjectBuilder()
                    .add("pagelen", 100)
                    .add("values", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                            .add("uuid", "{1}")
                            .add("url",
                                "https://self-xdsd.com/bitbucket"
                                    + "/amihaiemil/repo")
                            .build())
                        .build())
                    .build()
            )
        );
        final Provider provider = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");

        final Webhooks webhooks = provider
            .repo("amihaiemil", "repo")
            .webhooks();
        final Webhook  webhook = webhooks.iterator().next();

        MatcherAssert.assertThat(
            webhooks,
            Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
            webhook.id(),
            Matchers.equalTo("{1}")
        );
        MatcherAssert.assertThat(
            webhook.url(),
            Matchers.equalTo("https://self-xdsd.com/bitbucket/amihaiemil/repo")
        );

        final MockJsonResources.MockRequest req = res.requests().first();
        MatcherAssert.assertThat(
            req.getAccessToken().value(),
            Matchers.equalTo("Bearer bitbucket123")
        );
        MatcherAssert.assertThat(
            req.getMethod(),
            Matchers.equalTo("GET")
        );
        MatcherAssert.assertThat(
            req.getUri().toString(),
            Matchers.equalTo(
                "https://bitbucket.org/api/2.0"
                    + "/repositories/amihaiemil/repo/hooks?pagelen=100"
            )
        );
    }

    /**
     * BitbucketWebhooks is empty iterable due to 404 NOT FOUND.
     */
    @Test
    public void iteratesWebhooksNotFound() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Bitbucket(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            new MockJsonResources(
                new AccessToken.Bitbucket("bitbucket123"),
                req -> new MockJsonResources.MockResource(
                    HttpURLConnection.HTTP_NOT_FOUND,
                    Json.createObjectBuilder().build()
                )
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
     * BitbucketWebhooks doesn't remove anything if there is
     * no self-xdsd webhook present.
     */
    @Test
    public void doesNotRemoveNonSelfHooks() {
        final MockJsonResources resources =
            new MockJsonResources(req -> {
                if (req.getMethod().equals("DELETE")) {
                    throw new IllegalStateException(
                        "Only webhooks of self-xdsd can be removed"
                    );
                } else {
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("values", Json.createArrayBuilder()
                                .add(
                                    Json.createObjectBuilder()
                                        .add("uuid", "{123}")
                                        .add("url", "https://example.com")
                                        .build()
                                ).build())
                            .build()
                    );
                }
            });
        final URI uri = URI.create(
            "https://bitbucket.org/api/2.0"
                + "/repositories/amihaiemil/repo/hooks"
        );

        boolean removed = new BitbucketWebhooks(
            resources, uri, Mockito.mock(Storage.class)
        ).remove();

        MatcherAssert.assertThat(
            removed,
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * BitbucketWebhooks can remove all webhooks related to Self XDSD.
     */
    @Test
    public void removesSelfWebhooks() {
        final MockJsonResources resources =
            new MockJsonResources(req -> {
                final MockJsonResources.MockResource res;
                if (req.getMethod().equals("DELETE")) {
                    res = new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        JsonValue.NULL
                    );
                } else {
                    res = new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("values", Json.createArrayBuilder()
                                .add(
                                    Json.createObjectBuilder()
                                        .add("uuid", "{123}")
                                        .add("url", "https://self-xdsd.com"
                                                + "/bitbucket/amihaiemil/repo")
                                        .build()
                                ).build())
                            .build()
                    );
                }
                return res;
            });
        final URI uri = URI.create(
            "https://bitbucket.org/api/2.0"
                + "/repositories/amihaiemil/repo/hooks"
        );

        boolean removed = new BitbucketWebhooks(
            resources, uri, Mockito.mock(Storage.class)
        ).remove();

        MatcherAssert.assertThat(
            removed,
            Matchers.is(Boolean.TRUE)
        );

        final MockJsonResources.MockRequest deleteReq = resources.requests()
            .atIndex(1);
        MatcherAssert.assertThat(deleteReq.getMethod(),
            Matchers.equalTo("DELETE"));
        MatcherAssert.assertThat(deleteReq.getUri().toString(),
            Matchers.equalTo("https://bitbucket.org/api/2.0"
                + "/repositories/amihaiemil/repo/hooks/%7B123%7D"));
    }

    /**
     * BitbucketWebhooks#remove returns false if fails to remove all
     * hooks of Self XDSD.
     */
    @Test
    public void failsRemovingAllSelfWebhooks() {
        final MockJsonResources resources =
            new MockJsonResources(req -> {
                final MockJsonResources.MockResource res;
                if (req.getMethod().equals("DELETE")) {
                    res = new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        JsonValue.NULL
                    );
                } else {
                    res = new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("values", Json.createArrayBuilder()
                                .add(
                                    Json.createObjectBuilder()
                                        .add("uuid", "{123}")
                                        .add("url", "https://self-xdsd.com"
                                            + "/bitbucket/amihaiemil/repo")
                                        .build()
                                ).build())
                            .build()
                    );
                }
                return res;
            });
        final URI uri = URI.create(
            "https://bitbucket.org/api/2.0"
                + "/repositories/amihaiemil/repo/hooks"
        );

        boolean removed = new BitbucketWebhooks(
            resources, uri, Mockito.mock(Storage.class)
        ).remove();

        MatcherAssert.assertThat(
            removed,
            Matchers.is(Boolean.FALSE)
        );
    }
}