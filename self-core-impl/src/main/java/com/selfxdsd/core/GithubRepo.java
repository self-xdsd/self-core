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
import com.selfxdsd.api.Labels;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonObject;
import java.net.URI;

/**
 * A Github repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
final class GithubRepo extends BaseRepo {

    /**
     * Constructor.
     * @param resources The provider's JSON Resources.
     * @param uri URI Pointing to this repo.
     * @param owner Owner of this repo.
     * @param storage Storage used to save the Project when
     *  this repo is activated.
     */
    GithubRepo(
        final JsonResources resources,
        final URI uri,
        final User owner,
        final Storage storage
    ) {
        this(resources, uri, owner, null, storage);
    }

    /**
     * Constructor.
     * @param resources The provider's JSON Resources.
     * @param uri URI Pointing to this repo.
     * @param owner Owner of this repo.
     * @param json Repo JSON.
     * @param storage Storage used to save the Project when
     *  this repo is activated.
     */
    GithubRepo(
        final JsonResources resources,
        final URI uri,
        final User owner,
        final JsonObject json,
        final Storage storage
    ) {
        super(resources, uri, owner, json, storage);
    }

    @Override
    public Project activate() {
        final Project project = super.activate();
        project.resolve(
            new Event() {
                @Override
                public String type() {
                    return Type.ACTIVATE;
                }

                @Override
                public Issue issue() {
                    throw new UnsupportedOperationException(
                        "No Issue in the activate event."
                    );
                }

                @Override
                public Comment comment() {
                    throw new UnsupportedOperationException(
                        "No Comment in the activate event."
                    );
                }

                @Override
                public Commit commit() {
                    throw new UnsupportedOperationException(
                        "No Commit in the activate event."
                    );
                }

                @Override
                public String repoNewName() {
                    return null;
                }

                @Override
                public Project project() {
                    return project;
                }

            }
        );
        return project;
    }

    @Override
    public String fullName() {
        return this.json().getString("full_name");
    }

    @Override
    public Issues issues() {
        return new GithubIssues(
            this.resources(),
            URI.create(this.repoUri().toString() + "/issues"),
            this,
            this.storage()
        );
    }

    /**
     * {@inheritDoc}
     * <br/>
     * In the case of Github, the Pull Requests API is
     * identical with the Issues API (Github does not differentiate
     * between issues and pull requests).
     */
    @Override
    public Issues pullRequests() {
        return new GithubIssues(
            this.resources(),
            URI.create(this.repoUri().toString() + "/issues"),
            this,
            this.storage()
        );
    }

    @Override
    public Collaborators collaborators() {
        return new GithubCollaborators(
            this.resources(),
            URI.create(this.repoUri().toString() + "/collaborators"),
            this.storage()
        );
    }

    @Override
    public Webhooks webhooks() {
        return new GithubWebhooks(
            this.resources(),
            URI.create(this.repoUri().toString() + "/hooks"),
            this.storage()
        );
    }

    @Override
    public Stars stars() {
        return new GithubStars(
            this.resources(),
            URI.create(this
                .repoUri()
                .toString()
                .replace("repos", "user/starred")
            ),
            this.storage()
        );
    }

    @Override
    public Commits commits() {
        return new GithubCommits(
            this.resources(),
            URI.create(this.repoUri().toString() + "/commits"),
            this.storage()
        );
    }

    @Override
    public Labels labels() {
        return new GithubRepoLabels(
            URI.create(this.repoUri().toString() + "/labels"),
            this.resources()
        );
    }
}
