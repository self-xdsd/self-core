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

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import java.net.URI;
import java.util.Iterator;

/**
 * Unit tests for {@link GithubPersonalRepos}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.86
 */
public final class GithubPersonalReposTestCase {

    /**
     * Iterates only over personal repos. If the owner of a repo from the
     * response is an org, it should skipped.
     */
    @Test
    public void iteratesPersonalRepos() {
        final JsonObject first = Json.createObjectBuilder()
            .add("id", 1)
            .add("full_name", "amihaiemil/repo1")
            .add("owner", Json
                .createObjectBuilder()
                .add("login", "amihaiemil")
                .build())
            .build();
        final JsonObject second = Json.createObjectBuilder()
            .add("id", 3)
            .add("full_name", "amihaiemil/repo3")
            .add("owner", Json
                .createObjectBuilder()
                .add("login", "amihaiemil")
                .build())
            .build();
        final JsonResources resources = new MockJsonResources(
            request -> {
                MatcherAssert.assertThat(
                    request.getUri().toString(),
                    Matchers.equalTo(
                        "https://api.github.com/user/repos?per_page=100"
                    )
                );
                return new MockJsonResources.MockResource(200, Json
                    .createArrayBuilder()
                    .add(first)
                    .add(Json.createObjectBuilder()
                        .add("id", 2)
                        .add("full_name", "orgname/repo2")
                        .add("owner", Json
                            .createObjectBuilder()
                            .add("login", "orgname")
                            .build())
                        .build())
                    .add(second)
                    .build());
            }
        );
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Repos repos = new GithubPersonalRepos(
            URI.create("https://api.github.com"),
            user,
            resources,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repos,
            Matchers.iterableWithSize(2)
        );
        final Iterator<Repo> iterator = repos.iterator();
        MatcherAssert.assertThat(
            iterator.next().json(),
            Matchers.equalTo(first)
        );
        MatcherAssert.assertThat(
            iterator.next().json(),
            Matchers.equalTo(second)
        );
    }

    /**
     * The iterable is empty, because the response has no data.
     */
    @Test
    public void iteratesEmptyResponse() {
        final JsonResources resources = new MockJsonResources(
            request -> {
                MatcherAssert.assertThat(
                    request.getUri().toString(),
                    Matchers.equalTo(
                        "https://api.github.com/user/repos?per_page=100"
                    )
                );
                return new MockJsonResources.MockResource(
                    200,
                    Json.createArrayBuilder().build()
                );
            }
        );
        final User user = Mockito.mock(User.class);
        Mockito.when(user.username()).thenReturn("amihaiemil");
        final Repos repos = new GithubPersonalRepos(
            URI.create("https://api.github.com"),
            user,
            resources,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            repos,
            Matchers.emptyIterable()
        );
    }

}
