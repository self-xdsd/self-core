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
 */package com.selfxdsd.core;

import com.selfxdsd.api.ApiToken;
import com.selfxdsd.api.storage.Storage;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * Stored API Token.
 *
 * @author Andrei Osipov (andreoss@sdf.org)
 * @version $Id$
 * @since 0.0.61
 */
public final class StoredApiToken implements ApiToken {

    /**
     * The storage.
     */
    private final Storage storage;

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
        final Storage storage,
        final String name,
        final byte[] secret,
        final LocalDateTime expiration
    ) {
        this.storage = storage;
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
