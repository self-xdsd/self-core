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

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * Github repo collaborators.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 */
final class GithubCollaborators implements Collaborators {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        GithubCollaborators.class
    );

    /**
     * Github repo Collaborators base uri.
     */
    private final URI collaboratorsUri;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Self storage, in case we want to store something.
     */
    private final Storage storage;

    /**
     * Ctor.
     *
     * @param resources Github's JSON Resources.
     * @param collaboratorsUri Collaborators base URI.
     * @param storage Storage.
     */
    GithubCollaborators(
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
        final boolean result;
        LOG.debug(
            "Inviting Collaborator " + username + " with permission "
            + "to [" + this.collaboratorsUri.toString() + "]."
        );
        final Resource response = this.resources.put(
            URI.create(this.collaboratorsUri.toString() + "/" + username),
            Json.createObjectBuilder().build()
        );
        if(response.statusCode() == HttpURLConnection.HTTP_CREATED) {
            result = true;
            LOG.debug("Invitation successfully created!");
        } else if (response.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            result = true;
            LOG.debug("User was already invited, everything is ok.");
        } else {
            result = false;
            LOG.error(
                "Unexpected status when inviting user " + username
                + " to [" + this.collaboratorsUri.toString() + "]. "
                + "Expected 201 CREATED or 204 NO CONTENT, but got "
                + response.statusCode()
            );
        }
        return result;
    }

    /**
     * {@inheritDoc}
     * <br/>
     * <code>DELETE /repos/:owner/:repo/collaborators/:username</code>
     * <br/>
     * <a href="https://developer.github.com/v3/repos/collaborators/#remove-a-repository-collaborator">See</a>
     */
    @Override
    public boolean remove(final String username) {
        final boolean result;
        LOG.debug(
            "Removing Collaborator " + username
                + "from [" + this.collaboratorsUri.toString() + "]."
        );
        final Resource response = this.resources.delete(
            URI.create(this.collaboratorsUri.toString() + "/" + username),
            Json.createObjectBuilder().build()
        );
        if(response.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            result = true;
            LOG.debug("Collaborator successfully removed!");
        }else {
            result = false;
            LOG.error(
                "Unexpected status when removing collaborator " + username
                    + " from [" + this.collaboratorsUri.toString() + "]. "
                    + "Expected 204 NO CONTENT, but got "
                    + response.statusCode()
            );
        }
        return result;
    }

    @Override
    public Iterator<Collaborator> iterator() {
        throw new UnsupportedOperationException(
            "We can't iterate over a Github repo's collaborators for now."
        );
    }
}
