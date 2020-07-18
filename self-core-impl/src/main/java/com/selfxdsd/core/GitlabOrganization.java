package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

import javax.json.JsonObject;

/**
 * A Gitlab Provider Organization (group).
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
final class GitlabOrganization implements Organization {

    /**
     * The Organization in JSON format as returned by Gitlab's API.
     */
    private final JsonObject json;

    /**
     * Gitlab's JSON Resources.
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
     * @param json The Organization in JSON format as returned by Gitlab's API.
     * @param resources Gitlab's JSON Resources.
     * @param storage Storage used by Organization Repos.
     */
    GitlabOrganization(final User owner,
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
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public JsonObject json() {
        return this.json;
    }
}
