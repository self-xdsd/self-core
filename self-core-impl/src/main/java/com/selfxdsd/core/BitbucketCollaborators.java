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

import com.selfxdsd.api.Collaborator;
import com.selfxdsd.api.Collaborators;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Iterator;

/**
 * Bitbucket repo collaborators.
 * @author Ali Fellahi (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.68p
 *
 * @todo #1014:60min Finish the implementation & tests of this class when
 *  Bitbucket provides a way to manipulate collaborators through its API.
 *  Check the doc here: https://developer.atlassian.com/bitbucket/api/2/
 *  reference/resource/workspaces/%7Bworkspace%7D/members
 */
final class BitbucketCollaborators implements Collaborators {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        BitbucketCollaborators.class
    );

    /**
     * Bitbucket repo Collaborators base uri.
     */
    private final URI collaboratorsUri;

    /**
     * Bitbucket's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param resources Gitlab's JSON Resources.
     * @param collaboratorsUri Collaborators base URI.
     * @param storage Storage.
     */
    BitbucketCollaborators(
        final JsonResources resources,
        final URI collaboratorsUri,
        final Storage storage
    ) {
        this.resources = resources;
        this.collaboratorsUri = collaboratorsUri;
        this.storage = storage;
    }

    @Override
    public boolean invite(final String username) {
        throw new UnsupportedOperationException(
            "Current Bitbucket API doesn't support invitations through api :("
        );
    }

    @Override
    public boolean remove(final String username) {
        throw new UnsupportedOperationException(
            "Current Bitbucket API doesn't support this operation :("
        );
    }

    @Override
    public Iterator<Collaborator> iterator() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
