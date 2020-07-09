package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonObject;
import java.net.URI;

/**
 * A Github Provider Organization.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
final class GithubOrganization implements Organization {

    /**
     * The Organization in JSON format as returned by Github's API.
     */
    private final JsonObject json;

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Current authenticated User.
     */
    private final User owner;

    /**
     * Storage used by Organization Repos.
     */
    private final Storage storage;
    /**
     * Ctor.
     *
     * @param owner Current authenticated User.
     * @param json The Organization in JSON format as returned by Github's API.
     * @param resources Github's JSON Resources.
     * @param storage Storage used by Organization Repos.
     */
    GithubOrganization(final User owner,
                       final JsonObject json,
                       final JsonResources resources,
                       final Storage storage) {
        this.owner = owner;
        this.json = json;
        this.resources = resources;
        this.storage = storage;
    }

    @Override
    public String organizationId() {
        return Integer.toString(this.json.getInt("id"));
    }

    @Override
    public Repos repos() {
        final URI reposUri = URI.create(this.json.getString("repos_url"));
        return new GithubOrganizationRepos(reposUri,
            this.owner,
            this.resources,
            this.storage);
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
