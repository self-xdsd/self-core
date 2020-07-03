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
     * Organization repos.
     * @return Repos.
     */
    Repos repos();

    /**
     * The Organization in JSON format as returned by the provider's API.
     * @return JsonObject.
     */
    JsonObject json();

}
