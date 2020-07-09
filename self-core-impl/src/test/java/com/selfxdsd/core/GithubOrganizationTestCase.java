package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link GithubOrganization}.
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
public final class GithubOrganizationTestCase {

    /**
     * Returns organization data.
     */
    @Test
    public void returnsOrganizationData(){
        final JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .build();
        final Organization organization = new GithubOrganization(
            json,
            Mockito.mock(JsonResources.class)
        );

        MatcherAssert.assertThat(organization.organizationId(),
            Matchers.is("1"));
        MatcherAssert.assertThat(organization.json(),
            Matchers.is(json));
    }

    /**
     * Fetches organization repos.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void fetchesOrganizationRepos(){
        new GithubOrganization(Json.createObjectBuilder().build(),
            Mockito.mock(JsonResources.class)).repos();
    }

}
