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

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * A Github Provider Organization Repos.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
final class GithubOrganizationRepos implements Repos {

    /**
     * Organization Repos URI.
     */
    private final URI uri;

    /**
     * Current authenticated User.
     */
    private final User owner;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Storage used by Organization Repo.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param uri Organization Repos URI.
     * @param owner Current authenticated User.
     * @param resources Github's JSON Resources.
     * @param storage Storage used by Organization Repo.
     */
    GithubOrganizationRepos(final URI uri,
                            final User owner,
                            final JsonResources resources,
                            final Storage storage) {
        this.uri = uri;
        this.owner = owner;
        this.resources = resources;
        this.storage = storage;
    }


    @Override
    public Iterator<Repo> iterator() {
        final Resource resource = resources.get(this.uri);
        final JsonArray repos;
        final int statusCode = resource.statusCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            repos = resource.asJsonArray();
        } else {
            throw new IllegalStateException("Unable to fetch Github "
                + "organization Repos for current User. Expected 200 OK, "
                + "but got: " + statusCode);
        }
        return repos.stream()
            .filter(this::isAdmin)
            .map(this::buildRepo)
            .iterator();
    }

    /**
     * Checks if the User has admin rights in current Repo.
     *
     * @param repoData Repo as JSON.
     * @return Boolean.
     */
    private boolean isAdmin(final JsonValue repoData) {
        return ((JsonObject) repoData)
            .getJsonObject("permissions")
            .getBoolean("admin");
    }

    /**
     * Builds a GithubRepo from provided JSON data.
     *
     * @param repoData Repo as JSON.
     * @return Repo.
     */
    private Repo buildRepo(final JsonValue repoData) {
        final JsonObject json = (JsonObject) repoData;
        return new GithubRepo(
            this.resources,
            URI.create(json.getString("url")),
            this.owner,
            json,
            this.storage
        );
    }
}
