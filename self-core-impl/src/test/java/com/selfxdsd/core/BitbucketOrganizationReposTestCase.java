package com.selfxdsd.core;

import com.selfxdsd.api.Repos;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import com.selfxdsd.core.mock.MockJsonResources.MockResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonValue;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Unit tests for {@link BitbucketOrganizationRepos}.
 *
 * @author Ali FELLAHI (fellahi.ali@gmail.com)
 * @version $Id$
 * @since 0.0.68
 */
public final class BitbucketOrganizationReposTestCase {

    /**
     * Can fetches organization repos.
     */
    @Test
    public void fetchOk() {
        final JsonResources resources = new MockJsonResources(
            req -> {
                MatcherAssert.assertThat(
                    req.getMethod(),
                    Matchers.is("GET")
                );
                MatcherAssert.assertThat(
                    req.getUri().toString(),
                    Matchers.equalTo(
                        "https://bitbucket.org/api/2.0/repositories/alilosoft"
                    )
                );
                return new MockResource(
                    HttpURLConnection.HTTP_OK,
                    Json.createObjectBuilder().add(
                        "values",
                        Json.createArrayBuilder()
                            .add(Json.createObjectBuilder()
                                .add("slug", "repo1")
                                .build())
                            .add(Json.createObjectBuilder()
                                .add("slug", "repo2")
                                .build())
                            .add(Json.createObjectBuilder()
                                .add("slug", "repo3")
                                .build())
                            .build()
                        ).build()
                    );
            }
        );
        final Repos repos = new BitbucketOrganizationRepos(
            URI.create("https://bitbucket.org/api/2.0/repositories/alilosoft"),
            Mockito.mock(User.class),
            resources,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(repos, Matchers.iterableWithSize(3));
    }

    /**
     * Throw if organization repos are not fetched.
     */
    @Test(expected = IllegalStateException.class)
    public void fetchNotOk() {
        final JsonResources resources = new MockJsonResources(
            req -> new MockResource(404, JsonValue.NULL)
        );
        new BitbucketOrganizationRepos(
            URI.create("bad/uri"),
            Mockito.mock(User.class),
            resources,
            Mockito.mock(Storage.class)
        ).iterator();
    }

}
