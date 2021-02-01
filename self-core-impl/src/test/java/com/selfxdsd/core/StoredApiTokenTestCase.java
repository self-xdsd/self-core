package com.selfxdsd.core;

import com.selfxdsd.api.ApiToken;
import java.time.LocalDateTime;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

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
                "gh",
                new byte[]{'p', 'a', 's', 's'},
                LocalDateTime.MAX
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
                "gh",
                new byte[]{'p', 'a', 's', 's'},
                LocalDateTime.MAX
            ).expiration(),
            Matchers.is(LocalDateTime.MAX)
        );
    }

    /**
     * Returns secret.
     */
    @Test
    public void returnsSecret() {
        Assert.assertArrayEquals(
            new byte[]{'p', 'a', 's', 's'},
            new StoredApiToken(
                "gh",
                new byte[]{'p', 'a', 's', 's'},
                LocalDateTime.MAX
            ).secret()
        );
    }

    /**
     * Same StoredApiTokens are equatable.
     */
    @Test
    public void comparesStoredUserObjects() {
        final ApiToken first = new StoredApiToken(
            "user",
            new byte[]{'p', 'a', 's', 's'},
            LocalDateTime.MAX
        );
        final ApiToken second = new StoredApiToken(
            "user",
            new byte[]{'p', 'a', 's', 's'},
            LocalDateTime.MAX
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
            "user",
            new byte[]{'p', 'a', 's', 's'},
            LocalDateTime.MAX
        );
        final ApiToken second = new StoredApiToken(
            "user",
            new byte[]{'p', 'a', 'x', 'x'},
            LocalDateTime.MAX
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