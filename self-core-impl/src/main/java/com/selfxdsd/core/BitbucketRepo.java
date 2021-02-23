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
 * A Bitbucket repository.
 * @author criske
 * @version $Id$
 * @since 0.0.67
 * @todo #976:60min Implement and test BitbucketRepo#activate().
 * @todo #976:60min Start implement and test BitbucketIssues for
 *  BitbucketRepo#issues().
 * @todo #976:60min Start implement and test BitbucketCollaborators for
 *  BitbucketRepo#collaborators().
 * @todo #976:60min Start implement and test BitbucketWebhooks for
 *  BitbucketRepo#webhooks().
 * @todo #976:60min Start implement and test BitbucketStars for
 *  BitbucketRepo#stars().
 * @todo #976:60min Start implement and test BitbucketCommits for
 *  BitbucketRepo#commits().
 */
final class BitbucketRepo extends BaseRepo {

    /**
     * Constructor.
     * @param resources The provider's JSON Resources.
     * @param uri URI Pointing to this repo.
     * @param owner Owner of this repo.
     * @param storage Storage used to save the Project when
     *  this repo is activated.
     */
    BitbucketRepo(
        final JsonResources resources,
        final URI uri,
        final User owner,
        final Storage storage
    ) {
        super(resources, uri, owner, storage);
    }

    @Override
    public Project activate() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public String fullName() {
        return this.json().getString("full_name");
    }

    @Override
    public Issues issues() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Issues pullRequests() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Collaborators collaborators() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Webhooks webhooks() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Stars stars() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Commits commits() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Labels labels() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}