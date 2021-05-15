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
import java.util.Map;

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
     * Repo name.
     */
    private final String repo;

    /**
     * Owner name.
     */
    private final String owner;

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
        List<String> splits = Arrays.asList(starsUri.getPath().split("/"));
        this.owner = splits.get(splits.size() - 1);
        this.repo = splits.get(splits.size() - 2);
    }

    @Override
    public boolean add() {
        LOG.debug(
            "Starring Github repository " + this.owner
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
                "Unexpected status when starring repo " + this.repo
                    + " of user " + this.owner + " ]"
                    + "Expected 204 NO CONTENT, but got "
                    + response.statusCode()
            );
        }
        return starred;
    }

    /**
     * {@inheritDoc}
     * <br/>
     * See doc <a href="https://docs.github.com/en/rest/reference/activity#check-if-a-repository-is-starred-by-the-authenticated-user">here</a>.
     */
    @Override
    public boolean added() {
        LOG.debug(
            "Check if Github repository " + this.repo
                + " is starred by current authenticated user."
        );

        final boolean isStarred;
        final Resource response = this.resources.get(
            URI.create(this.starsUri.toString()),
            () -> Map.of("Accept", List.of("application/vnd.github.v3+json"))
        );
        final int status = response.statusCode();
        if (status == HttpURLConnection.HTTP_NO_CONTENT
            || status == HttpURLConnection.HTTP_OK) {
            isStarred = true;
            LOG.debug("Repo is starred by current authenticated user.");
        } else {
            isStarred = false;
            if (response.statusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                LOG.debug("Repo is not starred by current authenticated user.");
            } else {
                LOG.warn(
                    "Unexpected status when checking if repo " + this.repo
                        + " of user " + this.owner
                        + " is starred by current authenticated user."
                        + " Expected 204 NO CONTENT or 404 HTTP_NOT_FOUND,"
                        + " but got " + response.statusCode()
                );
            }
        }
        return isStarred;
    }

    @Override
    public String toString() {
        return this.starsUri.toString();
    }
}
