package com.selfxdsd.core;

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * A Gitlab Provider Organization Repos.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
final class GitlabOrganizationRepos implements Repos {

    /**
     * Organization Repos URI owned by current User.
     */
    private final URI uri;

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
     * @param organizationId Organization Id.
     * @param owner Current authenticated User.
     * @param resources Gitlab's JSON Resources.
     * @param storage Storage used by Organization Repo.
     */
    GitlabOrganizationRepos(final String organizationId,
                            final User owner,
                            final JsonResources resources,
                            final Storage storage) {
        this.uri = URI.create("https://gitlab.com/api/v4/groups/"
            + organizationId + "/projects?owned=true");
        this.owner = owner;
        this.resources = resources;
        this.storage = storage;
    }


    @Override
    public Iterator<Repo> iterator() {
        final Resource resource = resources.get(this.uri);
        final JsonArray repos;
        final int statusCode = resource.statusCode();
        if (statusCode == HttpURLConnection.HTTP_OK) {
            repos = resource.asJsonArray();
        } else {
            throw new IllegalStateException("Unable to fetch Gitlab "
                + "organization Repos for current User. Expected 200 OK, "
                + "but got: " + statusCode);
        }
        return repos.stream()
            .map(this::buildRepo)
            .iterator();
    }

    /**
     * Builds a GitlabRepo from provided JSON data.
     *
     * @param repoData Repo as JSON.
     * @return Repo.
     */
    private Repo buildRepo(final JsonValue repoData) {
        final String repoUri = ((JsonObject) repoData)
            .getJsonObject("_links").getString("self");
        return new GitlabRepo(this.resources,
            URI.create(repoUri),
            this.owner,
            this.storage
        );
    }
}
