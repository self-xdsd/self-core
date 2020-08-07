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

import java.net.URI;

/**
 * Gitlab as a Provider.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @todo #27:30min Continue adding integration tests for Gitlab provider.
 * @todo #300:30min Gitlab Invitations: implement and test invitations for
 *  Gitlab provider.
 */
public final class Gitlab implements Provider {

    /**
     * User.
     */
    private final User user;

    /**
     * Github's URI.
     */
    private final URI uri = URI.create("https://gitlab.com/api/v4");

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Storage where we might save some stuff.
     */
    private final Storage storage;

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     */
    public Gitlab(final User user, final Storage storage) {
        this(user, storage, new JsonResources.JdkHttp());
    }

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param resources Gitlab's JSON Resources.
     */
    public Gitlab(
        final User user,
        final Storage storage,
        final JsonResources resources
    ) {
        this(user, storage, resources, "");
    }

    /**
     * Private constructor. It can only be used by the withToken(...) method
     * to return an instance which has a token.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param resources Gitlab's JSON Resources.
     * @param accessToken Access token.
     */
    private Gitlab(
        final User user,
        final Storage storage,
        final JsonResources resources,
        final String accessToken
    ) {
        this.user = user;
        this.storage = storage;
        if (accessToken == null || accessToken.isBlank()) {
            this.resources = resources;
        } else {
            this.resources = resources
                .authenticated(new AccessToken.Gitlab(accessToken));
        }
    }

    @Override
    public String name() {
        return "gitlab";
    }

    @Override
    public Repo repo(final String owner, final String name) {
        return GitlabRepo.createFromName(
            owner,
            name,
            this.resources,
            this.user,
            this.storage);
    }

    @Override
    public Invitations invitations() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Organizations organizations() {
        return new GitlabOrganizations(
            this.resources,
            this.uri,
            this.user,
            this.storage);
    }

    @Override
    public Provider withToken(final String accessToken) {
        return new Gitlab(
            this.user,
            this.storage,
            this.resources,
            accessToken
        );
    }
}
