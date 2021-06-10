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
import com.selfxdsd.api.Resource;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.Iterator;

/**
 * Personal repos of the authenticated user, both public and private.
 * @author criske
 * @version $Id$
 * @since 0.0.86
 */
final class GitlabPersonalRepos implements Repos {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GitlabPersonalRepos.class
    );

    /**
     * Gitlab API base URI.
     */
    private final URI baseUri;

    /**
     * Current authenticated User.
     */
    private final User owner;

    /**
     * Gitlab's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Storage used by Organization Repo.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param baseUri Gitlab API base URI.
     * @param owner Current authenticated User.
     * @param resources Gitlab's JSON Resources.
     * @param storage Storage.
     */
    GitlabPersonalRepos(
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

    /**
     * {@inheritDoc}
     *
     * @see <a href="https://docs.gitlab.com/ee/api/projects.html#list-user-projects">here</a>
     *
     */
    @Override
    public Iterator<Repo> iterator() {
        final Iterator<Repo> iterator;
        final Resource authUser = this.resources
            .get(URI.create(this.baseUri + "/user"));
        if (authUser.statusCode() == HttpURLConnection.HTTP_OK) {
            final int userId = authUser.asJsonObject().getInt("id");
            iterator = new ResourcePaging.FromHeaders(
                this.resources,
                URI.create(this.baseUri + "/users/" + userId
                    + "/projects?owned=true&per_page=100")
            ).stream()
                .flatMap(repos -> repos.asJsonArray()
                    .stream()
                    .<Repo>map(repo -> new GitlabRepo(
                        this.resources,
                        URI.create(repo.asJsonObject().getJsonObject("_links")
                            .getString("self")),
                        this.owner,
                        repo.asJsonObject(),
                        this.storage
                    )))
                .iterator();
        } else {
            LOG.warn("Can't get user id - user is not authenticated "
                + "or something went wrong. Code ["
                + authUser.statusCode() + "]");
            iterator = Collections.emptyIterator();
        }
        return iterator;
    }
}