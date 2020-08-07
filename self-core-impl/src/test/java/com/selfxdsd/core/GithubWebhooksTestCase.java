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

import com.selfxdsd.api.Project;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import java.net.HttpURLConnection;

/**
 * Unit tests for {@link GithubWebhooks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
public final class GithubWebhooksTestCase {

    /**
     * A new Webhook can be added ok (receives CREATED).
     */
    @Test
    public void addsWebhookCreated() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Github(
            Mockito.mock(User.class),
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
                                .add(
                                    "events",
                                    Json.createArrayBuilder()
                                        .add("issues")
                                )
                                .add(
                                    "config",
                                    Json.createObjectBuilder()
                                        .add(
                                            "url",
                                            "https://self-xdsd.com/github/"
                                            + "amihaiemil/repo"
                                        )
                                        .add("content_type", "json")
                                        .add("secret", "webhook_tok333n")
                                ).build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/repos/amihaiemil/repo"
                                + "/hooks"
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
        MatcherAssert.assertThat(
            res, Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * A new Webhook is added, but receives status NOT FOUND.
     */
    @Test
    public void addsWebhookNotFound() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("amihaiemil/repo");
        Mockito.when(project.webHookToken()).thenReturn("webhook_tok333n");
        final Provider provider = new Github(
            Mockito.mock(User.class),
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
                                .add(
                                    "events",
                                    Json.createArrayBuilder()
                                        .add("issues")
                                )
                                .add(
                                    "config",
                                    Json.createObjectBuilder()
                                        .add(
                                            "url",
                                            "https://self-xdsd.com/github/"
                                                + "amihaiemil/repo"
                                        )
                                        .add("content_type", "json")
                                        .add("secret", "webhook_tok333n")
                                ).build()
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/repos/amihaiemil/repo"
                                + "/hooks"
                        )
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        Json.createObjectBuilder().build()
                    );
                }
            )
        );
        final boolean res = provider
            .repo("amihaiemil", "repo")
            .webhooks()
            .add(project);
        MatcherAssert.assertThat(
            res, Matchers.is(Boolean.FALSE)
        );
    }

}
