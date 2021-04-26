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

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.JsonValue;
import java.net.HttpURLConnection;

/**
 * Unit tests for {@link Github}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.76
 * @version $Id$
 */
public final class GithubTestCase {

    /**
     * Github can follow a user.
     */
    @Test
    public void canFollowUser() {
        final Provider github = new Github(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("PUT")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/user/following/vlad"
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonValue.EMPTY_JSON_OBJECT)
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_NO_CONTENT,
                        JsonValue.EMPTY_JSON_OBJECT
                    );
                }
            )
        );
        MatcherAssert.assertThat(
            github.follow("vlad"),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Github follow returns an unexpected status, the returned value is false.
     */
    @Test
    public void followReturnsUnexpectedStatus() {
        final Provider github = new Github(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            new MockJsonResources(
                req -> {
                    MatcherAssert.assertThat(
                        req.getMethod(),
                        Matchers.equalTo("PUT")
                    );
                    MatcherAssert.assertThat(
                        req.getUri().toString(),
                        Matchers.equalTo(
                            "https://api.github.com/user/following/vlad"
                        )
                    );
                    MatcherAssert.assertThat(
                        req.getBody(),
                        Matchers.equalTo(JsonValue.EMPTY_JSON_OBJECT)
                    );
                    return new MockJsonResources.MockResource(
                        HttpURLConnection.HTTP_BAD_METHOD,
                        JsonValue.EMPTY_JSON_OBJECT
                    );
                }
            )
        );
        MatcherAssert.assertThat(
            github.follow("vlad"),
            Matchers.is(Boolean.FALSE)
        );
    }

}
