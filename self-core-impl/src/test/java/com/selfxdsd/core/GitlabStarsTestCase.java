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
import com.selfxdsd.api.Stars;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link GitlabStars}.
 *
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.42
 */
public final class GitlabStarsTestCase {

    /**
     * GitlabStars.add() can add a star.
     */
    @Test
    public void canAddStar() {
        final Stars stars = new GitlabStars(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("repo/star")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonValue.EMPTY_JSON_OBJECT)
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_CREATED,
                        JsonValue.EMPTY_JSON_OBJECT
                    );
                }
            ),
            URI.create("repo/star"),
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            stars.add(),
            Matchers.is(true)
        );
    }

    /**
     * GitlabStars.add() works for already stared repo.
     */
    @Test
    public void canReStar() {
        final Stars stars = new GitlabStars(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("repo/star")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonValue.EMPTY_JSON_OBJECT)
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NOT_MODIFIED,
                        JsonValue.EMPTY_JSON_OBJECT
                    );
                }
            ),
            URI.create("repo/star"),
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            stars.add(),
            Matchers.is(true)
        );
    }

    /**
     * GitlabStars.add() returns false when star is not added (for any reason).
     */
    @Test
    public void starIsNotAdded() {
        final MockJsonResources.MockResource response =
            new MockJsonResources.MockResource(
                HttpURLConnection.HTTP_BAD_REQUEST,
                JsonValue.EMPTY_JSON_OBJECT
            );
        final Stars stars = new GitlabStars(
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("POST")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo("repo/star")
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonValue.EMPTY_JSON_OBJECT)
                    );
                    return response;
                }
            ),
            URI.create("repo/star"),
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            stars.add(),
            Matchers.is(false)
        );
        MatcherAssert.assertThat(
            response.statusCode(),
            Matchers.allOf(
                Matchers.not(HttpURLConnection.HTTP_CREATED),
                Matchers.not(HttpURLConnection.HTTP_NOT_MODIFIED)
            )
        );
    }
}
