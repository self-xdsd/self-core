package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonObject;
import java.net.URI;

/**
 * A Bitbucket Provider Organization.
 *
 * @author Nikita Monokov (nmonokov@gmail.com)
 * @version $Id$
 * @since 0.0.62
 */
final class BitbucketOrganization implements Organization {

    /**
     * The Organization in JSON format as returned by Bitbucket's API.
     */
    private final JsonObject json;

    /**
     * Bitbucket's JSON Resources.
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
     * @param json The Organization in JSON format as returned by Bitbucket's API.
     * @param resources Bitbucket's JSON Resources.
     * @param storage Storage used by Organization Repos.
     */
    BitbucketOrganization(final User owner,
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
        return this.json.getString("uuid");
    }

    @Override
    public Repos repos() {
        final URI reposUri = URI.create(this.json
                .get("links").asJsonObject()
                .get("repositories").asJsonObject()
                .getString("href"));
        return new BitbucketOrganizationRepos(reposUri,
            this.owner,
            this.resources,
            this.storage);
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
