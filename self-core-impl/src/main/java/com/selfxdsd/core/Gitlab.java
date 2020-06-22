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

import com.selfxdsd.api.Invitations;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import java.net.URI;

/**
 * Gitlab as a Provider.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @todo #27:30min Continue adding integration tests for Gitlab provider.
 */
public final class Gitlab implements Provider {

    /**
     * User.
     */
    private final User user;

    /**
     * Gitlab's URI.
     */
    private final URI uri;

    /**
     * Storage where we might save some stuff.
     */
    private final Storage storage;

    /**
     *Token used for making API Requests which require
     *user authentication.
     */
    private final String accessToken;

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     */
    public Gitlab(final User user, final Storage storage) {
        this(user, storage, URI.create("https://gitlab.com/api/v4"));
    }

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param uri Base URI of Gitlab's API.
     */
    public Gitlab(final User user, final Storage storage, final URI uri) {
        this(user, storage, uri, "");
    }

    /**
     * Private constructor. It can only be used by the withToken(...) method
     * to return an instance which has a token.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param uri Base URI of Gitlab's API.
     * @param accessToken Access token.
     */
    private Gitlab(
        final User user, final Storage storage,
        final URI uri, final String accessToken
    ) {
        this.user = user;
        this.uri = uri;
        this.storage = storage;
        this.accessToken = accessToken;
    }

    @Override
    public String name() {
        return "gitlab";
    }

    @Override
    public Repo repo(final String name) {
        final URI repo = URI.create(this.uri.toString() + "/projects/" + name);
        return new GitlabRepo(this.user, repo, this.storage);
    }

    @Override
    public Invitations invitations() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Provider withToken(final String accessToken) {
        return new Gitlab(this.user, this.storage, this.uri, accessToken);
    }
}
