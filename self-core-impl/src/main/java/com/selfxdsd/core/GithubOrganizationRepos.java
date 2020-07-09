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
 * A Github Provider Organization Repos.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
final class GithubOrganizationRepos implements Repos {

    /**
     * Organization Repos URI.
     */
    private final URI uri;

    /**
     * Current authenticated User.
     */
    private final User owner;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Storage used by Organization Repo.
     */
    private final Storage storage;

    /**
     * Ctor.
     * @param uri Organization Repos URI.
     * @param owner Current authenticated User.
     * @param resources Github's JSON Resources.
     * @param storage Storage used by Organization Repo.
     */
    GithubOrganizationRepos(final URI uri,
                            final User owner,
                            final JsonResources resources,
                            final Storage storage) {
        this.uri = uri;
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
            throw new IllegalStateException("Unable to fetch Github "
                + "organization Repos for current User. Expected 200 OK, "
                + "but got: " + statusCode);
        }
        return repos.stream()
            .filter(this::isAdmin)
            .map(this::buildRepo)
            .iterator();
    }

    /**
     * Checks if the User has admin rights in current Repo.
     *
     * @param repoData Repo as JSON.
     * @return Boolean.
     */
    private boolean isAdmin(final JsonValue repoData) {
        return ((JsonObject) repoData)
            .getJsonObject("permissions")
            .getBoolean("admin");
    }

    /**
     * Builds a GithubRepo from provided JSON data.
     *
     * @param repoData Repo as JSON.
     * @return Repo.
     */
    private Repo buildRepo(final JsonValue repoData) {
        final URI repoUri = URI.create(((JsonObject) repoData)
            .getString("url"));
        return new GithubRepo(this.resources,
            repoUri,
            this.owner,
            this.storage);
    }
}
