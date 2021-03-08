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
 * @since 0.0.68
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
