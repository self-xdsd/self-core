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
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import com.selfxdsd.core.mock.MockJsonResources.MockResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonStructure;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * Unit tests for {@link GitlabPersonalRepos}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.86
 */
public final class GitlabPersonalReposTestCase {

    /**
     * GitlabPersonalRepos is empty is user is not authenticated.
     */
    @Test
    public void returnsEmptyIteratorIfUserIsNotAuth() {
        final MockJsonResources res = new MockJsonResources(
            req -> new MockResource(
                HttpURLConnection.HTTP_UNAUTHORIZED,
                JsonStructure.EMPTY_JSON_OBJECT
            ));
        final GitlabPersonalRepos repos = new GitlabPersonalRepos(
            URI.create("https://gitlab.com/api/v4"),
            Mockito.mock(User.class),
            res,
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(repos, Matchers.emptyIterable());
    }


    /**
     * GitlabPersonalRepos returns an iterator with all personal repos.
     */
    @Test
    public void returnsIteratorWithAllPersonalRepos() {
        final MockJsonResources res = new MockJsonResources(
            req -> {
                final MockResource resp;
                if (req.getUri().toString()
                    .equals("https://gitlab.com/api/v4/user")) {
                    resp = new MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createObjectBuilder()
                            .add("id", 6018288)
                            .build()
                    );
                } else if (req.getUri().toString()
                    .equals("https://gitlab.com/api/v4/users"
                        + "/6018288/projects?owned=true&per_page=100")) {
                    resp = new MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createArrayBuilder()
                            .add(Json.createObjectBuilder()
                                .add("_links", Json
                                    .createObjectBuilder()
                                    .add(
                                        "self",
                                        "https://gitlab.com/api/v4"
                                            + "/projects/23826328"
                                    )
                                    .build())
                                .build())
                            .add(Json.createObjectBuilder()
                                .add("_links", Json
                                    .createObjectBuilder()
                                    .add(
                                        "self",
                                        "https://gitlab.com/api/v4"
                                            + "/projects/22690889"
                                    )
                                    .build())
                                .build())
                            .build()
                    );
                } else {
                    throw new UnsupportedOperationException();
                }
                return resp;
            });

        final GitlabPersonalRepos repos = new GitlabPersonalRepos(
            URI.create("https://gitlab.com/api/v4"),
            Mockito.mock(User.class),
            res,
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(repos, Matchers.iterableWithSize(2));

        final Iterator<Repo> iterator = repos.iterator();
        MatcherAssert.assertThat(
            iterator.next().toString(),
            Matchers.equalTo("https://gitlab.com/api/v4"
                + "/projects/23826328"
            )
        );
        MatcherAssert.assertThat(
            iterator.next().toString(),
            Matchers.equalTo("https://gitlab.com/api/v4"
                + "/projects/22690889"
            )
        );
    }

}