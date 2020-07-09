package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Organizations;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Iterator;

/**
 * A Github Provider Organizations.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
public final class GithubOrganizations implements Organizations {

    /**
     * Github's JSON Resources.
     */
    private final JsonResources resources;

    /**
     * Organizations URI.
     */
    private final URI uri;

    /**
     * Ctor.
     *
     * @param resources Github's JSON Resources.
     * @param uri Organizations URI..
     */
    public GithubOrganizations(final JsonResources resources,
                               final URI uri) {
        this.uri = uri;
        this.resources = resources;
    }

    @Override
    public Iterator<Organization> iterator() {
        final Resource resource = this.resources.get(this.uri);
        final JsonArray organizations;
        final int statusCode = resource.statusCode();
        switch (statusCode) {
            case HttpURLConnection.HTTP_OK:
                organizations = resource.asJsonArray();
                break;
            case HttpURLConnection.HTTP_UNAUTHORIZED:
                throw new IllegalStateException("Current User is "
                    + "not authenticated.");
            default:
                throw new IllegalStateException("Unable to fetch Github "
                    + "organizations for current User. Expected 200 OK, "
                    + "but got: " + statusCode);
        }
        return organizations
            .stream()
            .map(o -> (Organization) new GithubOrganization((JsonObject) o,
                this.resources))
            .iterator();
    }
}
