package com.selfxdsd.api;

import javax.json.JsonObject;

/**
 * Provider organization of which authenticated User has admin rights.
 * @author criske
 * @version $Id$
 * @since 0.0.8
 */
public interface Organization {

    /**
     * Organization ID.
     * @return String.
     */
    String organizationId();

    /**
     * Organization name.
     * @return String
     */
    String name();

    /**
     * Organization description.
     * @return String.
     */
    String description();

    /**
     * Organization repos.
     * @return OrganizationRepos.
     */
    OrganizationRepos repos();

    /**
     * The Organization in JSON format as returned by the provider's API.
     * @return JsonObject.
     */
    JsonObject json();
}
