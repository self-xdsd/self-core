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
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Personal repos of the authenticated user, both public and private.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.86
 */
final class GithubPersonalRepos implements Repos {

    /**
     * Github API base URI.
     */
    private final URI baseUri;

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
     * @param baseUri Github API base URI.
     * @param owner Current authenticated User.
     * @param resources Github's JSON Resources.
     * @param storage Storage used by Organization Repo.
     */
    GithubPersonalRepos(
        final URI baseUri,
        final User owner,
        final JsonResources resources,
        final Storage storage
    ) {
        this.baseUri = baseUri;
        this.owner = owner;
        this.resources = resources;
        this.storage = storage;
    }

    @Override
    public Iterator<Repo> iterator() {
        final List<Repo> repos = new ArrayList<>();
        final ResourcePaging paginated = new ResourcePaging.FromHeaders(
            this.resources,
            URI.create(
                this.baseUri + "/user/repos?per_page=100"
            )
        );
        final Iterator<Resource> pages = paginated.iterator();
        while(pages.hasNext()) {
            final JsonArray page = pages.next().asJsonArray();
            for (final JsonValue value : page) {
                final JsonObject repo = (JsonObject) value;
                final String login = repo.getJsonObject("owner").getString(
                    "login", ""
                );
                if(login.equalsIgnoreCase(this.owner.username())) {
                    repos.add(
                        new GithubRepo(
                            this.resources,
                            URI.create(
                                this.baseUri + "/"
                                + repo.getString("full_name")
                            ),
                            this.owner,
                            repo,
                            this.storage
                        )
                    );
                }
            }
        }
        return repos.iterator();
    }
}
