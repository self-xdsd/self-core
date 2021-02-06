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

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

import java.net.URI;

/**
 * Bitbucket as a Provider.
 * @author criske
 * @version $Id$
 * @since 0.0.62
 * @todo #943:60min Start scaffolding BitbucketRepo using the other providers
 *  as model.
 *  Documentation reference:
 *  https://developer.atlassian.com/bitbucket/api/2/reference/resource/repositories
 * @todo #943:60min Start scaffolding BitbucketInvitations using the other
 *  providers as model.
 *  Warning: 2.0 API doesn't support yet invitations.Until then we will use 1.0,
 *  so make sure you're using https://api.bitbucket.org/1.0/invitations
 *  when you're constructing the URI.
 *  Documentation reference:
 *  https://support.atlassian.com/bitbucket-cloud/docs/invitations-resource/
 * @todo #943:60min Start scaffolding BitbucketOrganizations using the other
 *  providers as model.
 *  Documentation reference:
 *  https://developer.atlassian.com/bitbucket/api/2/reference/resource/teams
 */
public final class Bitbucket implements Provider {

    /**
     * User.
     */
    private final User user;

    /**
     * Bitbucket's URI.
     */
    private final URI uri = URI.create("https://bitbucket.org/api/2.0");

    /**
     * Bitbucket's JSON Resources.
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
    public Bitbucket(final User user, final Storage storage) {
        this(user, storage, new JsonResources.JdkHttp());
    }

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param resources Github's JSON resources.
     */
    public Bitbucket(
        final User user, final Storage storage,
        final JsonResources resources
    ) {
        this(user, storage, resources, "");
    }

    /**
     * Private ctor, it can only be used by the withToken(...) method
     * to return an instance which has a token.
     * @param user User.
     * @param storage Self Storage
     * @param resources Bitbucket's JSON Resources.
     * @param accessToken Access token.
     */
    private Bitbucket(
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
                .authenticated(new AccessToken.Bitbucket(accessToken));
        }
    }

    @Override
    public String name() {
        return Names.BITBUCKET;
    }

    @Override
    public Repo repo(final String owner, final String name) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Invitations invitations() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Organizations organizations() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Provider withToken(final String accessToken) {
        return new Bitbucket(
            new BaseSelf.Authenticated(this.user, accessToken),
            this.storage,
            this.resources,
            accessToken
        );
    }
}