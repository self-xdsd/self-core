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

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Github as a Provider.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class Github implements Provider {

    /**
     * User.
     */
    private final User user;

    /**
     * Github's URI.
     */
    private final URI uri = URI.create("https://api.github.com");

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
    public Github(final User user, final Storage storage) {
        this(
            user,
            storage,
            new ConditionalJsonResources(
                new JsonResources.JdkHttp(),
                storage.jsonStorage()
            )
        );
    }

    /**
     * Constructor.
     * @param user Authenticated user.
     * @param storage Storage where we might save some stuff.
     * @param resources Github's JSON resources.
     */
    public Github(
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
     * @param resources Github's JSON Resources.
     * @param accessToken Access token.
     */
    private Github(
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
                .authenticated(new AccessToken.Github(accessToken));
        }
    }

    @Override
    public String name() {
        return "github";
    }

    @Override
    public Repos repos() {
        return null;
    }

    @Override
    public Repo repo(final String owner, final String name) {
        final URI repo = URI.create(
            this.uri + "/repos/" + owner + "/" + name
        );
        return new GithubRepo(
            this.resources, repo, this.user, this.storage
        );
    }

    @Override
    public Invitations invitations() {
        return new GithubRepoInvitations(
            this.resources,
            URI.create(
                this.uri.toString() + "/user/repository_invitations"
            ),
            this
        );
    }

    @Override
    public Organizations organizations() {
        return new GithubOrganizations(
            this.resources,
            this.uri,
            this.user,
            this.storage);
    }

    @Override
    public boolean follow(final String username) {
        final boolean followed;
        if(username == null || username.isEmpty()) {
            followed = false;
        } else {
            final Resource response = this.resources.put(
                URI.create(
                    this.uri + "/user/following/" + username
                ),
                Json.createObjectBuilder().build()
            );
            final int status = response.statusCode();
            if (status == HttpURLConnection.HTTP_NO_CONTENT) {
                followed = true;
            } else if (status == HttpURLConnection.HTTP_NOT_MODIFIED){
                followed = true;
            } else {
                followed = false;
            }
        }
        return followed;
    }

    @Override
    public Provider withToken(final String accessToken) {
        return new Github(
            new BaseSelf.Authenticated(this.user, accessToken),
            this.storage,
            this.resources,
            accessToken
        );
    }

    /**
     * Values of permission levels.
     * @see <a href="https://developer.github.com/v3/repos/collaborators/#add-a-repository-collaborator">here</a>
     */
    public static final class Permissions {
        /**
         * Hidden ctor.
         */
        private Permissions(){ }

        /**
         * Pull.
         */
        public static final String PULL = "pull";

        /**
         * Push.
         */
        public static final String PUSH = "push";

        /**
         * ADmin.
         */
        public static final String ADMIN = "admin";

        /**
         * Maintener.
         */
        public static final String MAINTAIN = "maintain";

        /**
         * Triage.
         */
        public static final String TRIAGE = "triage";
    }
}
