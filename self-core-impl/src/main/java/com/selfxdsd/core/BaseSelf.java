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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

/**
 * Base Self implementation.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
abstract class BaseSelf implements Self {

    /**
     * Self's storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param storage Storage for Self.
     */
    BaseSelf(final Storage storage) {
        this.storage = storage;
    }

    /**
     * Get Self's storage.
     * @return Storage.
     */
    Storage storage() {
        return this.storage;
    }

    @Override
    public User login(final Login login) {
        User signedUp = this.storage.users().signUp(
            login.username(),
            login.provider(),
            login.email()
        );
        if(login.accessToken() != null && !login.accessToken().isBlank()) {
            signedUp = new Authenticated(signedUp, login.accessToken());
        }
        return signedUp;
    }

    @Override
    public ProjectManagers projectManagers() {
        return this.storage.projectManagers();
    }

    @Override
    public Projects projects(){
        return this.storage.projects();
    }

    @Override
    public void close() throws Exception {
        this.storage.close();
    }
    /**
     * User authenticated with an access token from the provider.
     */
    static class Authenticated implements User {

        /**
         * Authenticated user.
         */
        private final User user;

        /**
         * Access token.
         */
        private final String accessToken;

        /**
         * Ctor.
         * @param user User.
         * @param accessToken Access token.
         */
        Authenticated(final User user, final String accessToken) {
            this.user = user;
            this.accessToken = accessToken;
        }

        @Override
        public String username() {
            return this.user.username();
        }

        @Override
        public String email() {
            return this.user.email();
        }

        @Override
        public Provider provider() {
            return this.user.provider().withToken(this.accessToken);
        }

        @Override
        public Projects projects() {
            return this.user.projects();
        }
    }

}
