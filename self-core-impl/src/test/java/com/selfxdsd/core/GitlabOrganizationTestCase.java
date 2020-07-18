package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

/**
 * Unit tests for {@link GitlabOrganization}.
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
public final class GitlabOrganizationTestCase {

    /**
     * Returns organization data.
     */
    @Test
    public void returnsOrganizationData(){
        final JsonObject json = Json.createObjectBuilder()
            .add("id", 1)
            .build();
        final Organization organization = new GitlabOrganization(
            Mockito.mock(User.class),
            json,
            Mockito.mock(JsonResources.class),
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(organization.organizationId(),
            Matchers.is("1"));
        MatcherAssert.assertThat(organization.json(),
            Matchers.is(json));
    }

    /**
     * Fetches organization repos.
     */
    @Test
    public void fetchesOrganizationRepos(){
        final Repos repos = new GitlabOrganization(
            Mockito.mock(User.class),
            Json.createObjectBuilder().add("id", 1).build(),
            Mockito.mock(JsonResources.class),
            Mockito.mock(Storage.class)
        ).repos();
        MatcherAssert.assertThat(repos,
            Matchers.instanceOf(GitlabOrganizationRepos.class));
    }

}
