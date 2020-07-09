package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Repos;

import javax.json.JsonObject;

/**
 * A Github Provider Organization.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 * @todo #122:30 Continue to implement and test Github Organization Repos for
 *  the authenticated User.
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
     * Ctor.
     *
     * @param json The Organization in JSON format as returned by Github's API.
     * @param resources Github's JSON Resources.
     */
    GithubOrganization(final JsonObject json,
                              final JsonResources resources) {
        this.json = json;
        this.resources = resources;
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
