package com.selfxdsd.core;

import com.selfxdsd.api.Organization;
import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;

import static com.selfxdsd.core.mock.MockJsonResources.*;

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
     * Fetches organization repos. Checks if repos url is
     * correctly extracted from organization JSON data and passed to
     * GithubOrganizationRepos constructor.
     */
    @Test
    public void fetchesOrganizationRepos(){
        final JsonResources resources = new MockJsonResources(
            req -> {
                MatcherAssert.assertThat(req.getUri().toString(),
                    Matchers.is("https://api.github.com/orgs/github/repos"));
                return new MockResource(200,
                    Json.createArrayBuilder().build());
            }
        );
        final Repos repos = new GithubOrganization(Mockito.mock(User.class),
            Json.createObjectBuilder()
                .add("id", 1)
                .add("repos_url", "https://api.github.com/orgs/github/repos")
                .build(),
            resources,
            Mockito.mock(Storage.class)).repos();

        repos.iterator();

        MatcherAssert.assertThat(repos,
            Matchers.instanceOf(GithubOrganizationRepos.class));
    }

}
