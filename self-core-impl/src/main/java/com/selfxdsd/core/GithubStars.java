package com.selfxdsd.core;

import com.selfxdsd.api.Stars;
import com.selfxdsd.api.storage.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

/**
 * Github stars.
 * @version $Id$
 * @since 0.0.30
 */
final class GithubStars implements Stars {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
            GithubStars.class
    );

    /**
     * Github repo stars base uri.
     */
    private final URI starsUri;

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
     * @param starsUri Stars base URI.
     * @param storage Storage.
     */
    GithubStars(
            final JsonResources resources,
            final URI starsUri,
            final Storage storage
    ) {
        this.resources = resources;
        this.starsUri = starsUri;
        this.storage = storage;
    }

    @Override
    public boolean add() {
        List<String> splits = Arrays.asList(this.starsUri.getPath().split("/"));
        String owner = splits.get(splits.size()-1);
        String repoName = splits.get(splits.size()-2);

        LOG.debug(
            "Starring Github repository " + repoName
        );

        final boolean starred;
        final Resource response = this.resources.put(
                URI.create(this.starsUri.toString()),
                Json.createObjectBuilder().build()
        );
        if (response.statusCode() == HttpURLConnection.HTTP_NO_CONTENT) {
            starred = true;
            LOG.debug("Repo was successfully starred.");
        } else {
            starred = false;
            LOG.error(
                "Unexpected status when starring repo " + repoName
                + " of user " + owner + " ]"
                + "Expected 204 NO CONTENT, but got "
                + response.statusCode()
            );
        }
        return starred;
    }
}
