package com.selfxdsd.core;

import com.selfxdsd.api.ApiToken;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * Stored API Token.
 */
public final class StoredApiToken implements ApiToken {
    /**
     * Token's name.
     */
    private final String name;

    /**
     * Secret.
     */
    private final byte[] secret;

    /**
     * Expiration date.
     */
    private final LocalDateTime expiration;

    /**
     * Ctor.
     *
     * @param name Name of token.
     * @param secret Value of token.
     * @param expiration Expiration date.
     */
    public StoredApiToken(
        final String name,
        final byte[] secret,
        final LocalDateTime expiration
    ) {
        this.name = name;
        this.secret = secret;
        this.expiration = expiration;
    }


    @Override
    public String name() {
        return this.name;
    }

    @Override
    public byte[] secret() {
        return Arrays.copyOf(this.secret, this.secret.length);
    }

    @Override
    public LocalDateTime expiration() {
        return this.expiration;
    }

    @Override
    public boolean equals(final Object other) {
        final boolean result;
        if (this == other) {
            result = true;
        } else if (other == null || getClass() != other.getClass()) {
            result = false;
        } else {
            final StoredApiToken that = (StoredApiToken) other;
            result = Objects.equals(name, that.name)
                && Arrays.equals(secret, that.secret)
                && Objects.equals(expiration, that.expiration);
        }
        return result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            this.expiration,
            this.name,
            Arrays.hashCode(this.secret)
        );
    }
}
