/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 *
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

import com.selfxdsd.api.ApiToken;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link StoredApiToken}.
 */
public final class StoredApiTokenTestCase {
    /**
     * Returns name.
     */
    @Test
    public void returnsName() {
        MatcherAssert.assertThat(
            new StoredApiToken(
                Mockito.mock(Storage.class),
                "gh",
                "pass",
                LocalDateTime.MAX,
                Mockito.mock(User.class)
            ).name(),
            Matchers.is("gh")
        );
    }

    /**
     * Returns expiration date.
     */
    @Test
    public void returnsExpirationDate() {
        MatcherAssert.assertThat(
            new StoredApiToken(
                Mockito.mock(Storage.class),
                "gh",
                "pass",
                LocalDateTime.MAX,
                Mockito.mock(User.class)
            ).expiration(),
            Matchers.is(LocalDateTime.MAX)
        );
    }

    /**
     * Returns token.
     */
    @Test
    public void returnsToken() {
        MatcherAssert.assertThat(
            new StoredApiToken(
                Mockito.mock(Storage.class),
                "gh",
                "pass",
                LocalDateTime.MAX,
                Mockito.mock(User.class)
            ).token(),
            Matchers.is("pass")
        );
    }

    /**
     * Returns owner.
     */
    @Test
    public void returnsOwner() {
        final User owner = Mockito.mock(User.class);
        MatcherAssert.assertThat(
            new StoredApiToken(
                Mockito.mock(Storage.class),
                "gh",
                "pass",
                LocalDateTime.MAX,
                owner
            ).owner(),
            Matchers.is(owner)
        );
    }

    /**
     * Same StoredApiTokens are equatable.
     */
    @Test
    public void comparesStoredUserObjects() {
        final ApiToken first = new StoredApiToken(
            Mockito.mock(Storage.class),
            "user",
            "pass",
            LocalDateTime.MAX,
            Mockito.mock(User.class)
        );
        final ApiToken second = new StoredApiToken(
            Mockito.mock(Storage.class),
            "user",
            "pass",
            LocalDateTime.MAX,
            Mockito.mock(User.class)
        );
        MatcherAssert.assertThat(
            first,
            Matchers.equalTo(second)
        );
        MatcherAssert.assertThat(
            first.hashCode(),
            Matchers.equalTo(second.hashCode())
        );
    }

    /**
     * Different StoredApiTokens are not equatable.
     */
    @Test
    public void comparesStoredUserObjectsNegative() {
        final ApiToken first = new StoredApiToken(
            Mockito.mock(Storage.class),
            "user",
            "pass",
            LocalDateTime.MAX,
            Mockito.mock(User.class)
        );
        final ApiToken second = new StoredApiToken(
            Mockito.mock(Storage.class),
            "user",
            "paxx",
            LocalDateTime.MAX,
            Mockito.mock(User.class)
        );
        MatcherAssert.assertThat(
            first,
            Matchers.not(second)
        );
        MatcherAssert.assertThat(
            first.hashCode(),
            Matchers.not(second.hashCode())
        );
    }
}