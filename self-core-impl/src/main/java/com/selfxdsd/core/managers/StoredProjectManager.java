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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.Github;
import com.selfxdsd.core.Gitlab;

/**
 * A Project Manager stored in Self. Use this class when implementing
 * the Storage.<br><br>
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredProjectManager implements ProjectManager {

    /**
     * This PMs id.
     */
    private final int id;

    /**
     * This PM's username.
     */
    private String username;

    /**
     * Provider's name.
     */
    private final String provider;

    /**
     * This PM's access token.
     */
    private final String accessToken;

    /**
     * Self's storage.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param id PM's id.
     * @param username PM's username.
     * @param provider The provider's name (Gitlab, Github etc).
     * @param accessToken API Access token.
     * @param storage Self's storage.
     * @checkstyle ParameterNumber (10 lines)
     */
    public StoredProjectManager(
        final int id,
        final String username,
        final String provider,
        final String accessToken,
        final Storage storage
    ) {
        this.id = id;
        this.username = username;
        this.provider = provider;
        this.accessToken = accessToken;
        this.storage = storage;
    }

    @Override
    public int id() {
        return this.id;
    }

    @Override
    public String username() {
        return this.username;
    }

    @Override
    public Provider provider() {
        final Provider provider;
        if(this.provider.equals(Provider.Names.GITHUB)) {
            provider = new Github(new PmUser(this), this.storage);
        } else {
            provider = new Gitlab(new PmUser(this), this.storage);
        }
        return provider.withToken(this.accessToken);
    }

    @Override
    public Project assign(final Repo repo) {
        return this.storage.projects().register(repo, this);
    }

    @Override
    public Projects projects() {
        return this.storage.projects().assignedTo(this.id);
    }

    /**
     * PM as a User.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.1
     */
    private final class PmUser implements User {

        /**
         * The PM.
         */
        private final ProjectManager manager;

        /**
         * Constructor.
         * @param manager PM acting as a user.
         */
        PmUser(final ProjectManager manager) {
            this.manager = manager;
        }

        @Override
        public String username() {
            return this.manager.username();
        }

        @Override
        public String email() {
            return null;
        }

        @Override
        public Provider provider() {
            return StoredProjectManager.this.provider();
        }

        @Override
        public Projects projects() {
            return this.manager.projects();
        }
    }
}
