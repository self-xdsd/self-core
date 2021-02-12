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

import static com.selfxdsd.core.mock.MockJsonResources.MockResource;

/**
 * Unit tests for {@link BitbucketOrganization}.
 *
 * @author Nikita Monokov(nmonokov@gmail.com)
 * @version $Id$
 * @since 0.0.62
 */
public final class BitbucketOrganizationTestCase {

    /**
     * Returns organization data.
     */
    @Test
    public void returnsOrganizationData(){
        final JsonObject json = Json.createObjectBuilder()
            .add("uuid", "{1234-5678}")
            .build();
        final Organization organization = new BitbucketOrganization(
            Mockito.mock(User.class),
            json,
            Mockito.mock(JsonResources.class),
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(organization.organizationId(),
            Matchers.is("{1234-5678}"));
        MatcherAssert.assertThat(organization.json(),
            Matchers.is(json));
    }

    /**
     * Returns organization repos.
     */
    @Test
    public void returnsOrganizationRepos(){
        final JsonResources resources = new MockJsonResources(
            req -> {
                MatcherAssert.assertThat(
                    req.getUri().toString(),
                    Matchers.is(
                        "https://bitbucket.org/!api/2.0/repositories"
                    )
                );
                return new MockResource(
                    200,
                    Json.createArrayBuilder().build()
                );
            }
        );
        final Repos repos = new BitbucketOrganization(Mockito.mock(User.class),
            Json.createObjectBuilder()
                .add("uuid", "{1234-5678}")
                .add("links",
                    Json.createObjectBuilder().add("repositories",
                        Json.createObjectBuilder().add("href",
                            "https://bitbucket.org/!api/2.0/repositories"
                        )
                    )
                )
                .build(),
            resources,
            Mockito.mock(Storage.class)).repos();

        MatcherAssert.assertThat(repos,
            Matchers.instanceOf(BitbucketOrganizationRepos.class));
    }

}
