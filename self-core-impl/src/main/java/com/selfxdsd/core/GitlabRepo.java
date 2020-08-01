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

import com.selfxdsd.api.Collaborators;
import com.selfxdsd.api.Issues;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import java.net.URI;

/**
 * A Gitlab repository.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
final class GitlabRepo extends BaseRepo {

    /**
     * Constructor.
     * @param resources Gitlab's JSON Resources.
     * @param uri URI Pointing to this repo.
     * @param owner Owner of this repo.
     * @param storage Storage used to save the Project when
     *  this repo is activated.
     */
    GitlabRepo(
        final JsonResources resources,
        final URI uri,
        final User owner,
        final Storage storage
    ) {
        super(resources, uri, owner, storage);
    }

    /**
     * Creates a Gitlab Repo from name.
     * @param resources Gitlab's JSON Resources.
     * @param repoName Repository name.
     * @param owner Owner of this repo.
     * @param storage Storage used to save the Project when
     *  this repo is activated.
     * @return GitlabRepo.
     */
    static GitlabRepo createFromName(final String repoName,
                                     final JsonResources resources,
                                     final User owner,
                                     final Storage storage) {
        final URI repo = URI.create("https://gitlab.com/api/v4/projects/"
            + owner.username() + "%2F"+repoName);
        return new GitlabRepo(resources, repo, owner, storage);
    }

    @Override
    public Project activate() {
        return this.storage().projectManagers().pick(provider()).assign(this);
    }

    @Override
    public String fullName() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Issues issues() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Collaborators collaborators() {
        final URI uri = URI.create(this.repoUri().toString() +"/members");
        return new GitlabCollaborators(this.resources(), uri, this.storage());
    }

}
