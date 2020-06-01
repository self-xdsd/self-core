/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core.mock;

import com.selfxdsd.api.User;
import com.selfxdsd.api.Users;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.StoredUser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * In memory users used for unit testing.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryUsers implements Users {

    /**
     * User's "table".
     */
    private final Map<UserKey, User> users;

    /**
     * Parent storage.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param storage Parent storage.
     */
    public InMemoryUsers(final Storage storage) {
        this.users = new HashMap<>();
        this.storage = storage;
    }

    @Override
    public User signUp(
        final String username,
        final String provider,
        final String email,
        final String accessToken
    ) {
        final UserKey key = new UserKey(
            username, provider
        );
        User signedUp = this.users.get(key);
        if(signedUp == null) {
            signedUp = new StoredUser(
                username, email, provider, accessToken, this.storage
            );
            this.users.put(
                key, signedUp
            );
        }
        return signedUp;
    }

    @Override
    public User user(final String username, final String provider) {
        return this.users.get(new UserKey(username, provider));
    }

    @Override
    public Iterator<User> iterator() {
        return this.users.values().iterator();
    }

    /**
     * User primary key.
     * @checkstyle VisibilityModifier (50 lines)
     */
    private static class UserKey {

        /**
         * Username.
         */
        final String username;
        /**
         * Provider.
         */
        final String provider;

        /**
         * Constructor.
         * @param username Given username.
         * @param provider Given provider.
         */
        UserKey(final String username, final String provider) {
            this.username = username;
            this.provider = provider;
        }

        @Override
        public boolean equals(final Object object) {
            if (this == object) {
                return true;
            }
            if (object == null || getClass() != object.getClass()) {
                return false;
            }
            final UserKey userKey = (UserKey) object;
            return username.equals(userKey.username)
                && provider.equals(userKey.provider);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, provider);
        }
    }
}
