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
import java.net.URI;

/**
 * Unit tests for {@link GithubOrganizationRepos}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
public final class GithubOrganizationReposTestCase {

    /**
     * Fetches organization repos data where User has admin rights.
     */
    @Test
    public void fetchesAndFilterOk() {
        final JsonResources resources = new MockJsonResources(
            r -> new MockResource(200, Json
                .createArrayBuilder()
                .add(Json.createObjectBuilder()
                    .add("url", "https://api.github.com/repos/github/media")
                    .add("permissions", Json.createObjectBuilder()
                        .add("admin", true))
                    .build())
                .add(Json.createObjectBuilder()
                    .add("url", "https://api.github.com/repos/github/albino")
                    .add("permissions", Json.createObjectBuilder()
                        .add("admin", true))
                    .build())
                .add(Json.createObjectBuilder()
                    .add("url", "https://api.github.com/repos/github/hubahuba")
                    .add("permissions", Json.createObjectBuilder()
                        .add("admin", false))
                    .build())
                .build())
        );
        final Repos repos = () -> new GithubOrganizationRepos(
            URI.create("https://api.github.com/orgs/github/repos"),
            Mockito.mock(User.class),
            resources,
            Mockito.mock(Storage.class)
        ).iterator();

        MatcherAssert.assertThat("User should have admin permission"
               +" in 2 out 3 repos", repos, Matchers.iterableWithSize(2));

    }

    /**
     * Throw if organization repos are not fetched.
     */
    @Test(expected = IllegalStateException.class)
    public void fetchesNotOk(){
        final JsonResources resources = new MockJsonResources(
            r -> new MockResource(404, JsonValue.NULL)
        );
        new GithubOrganizationRepos(
            URI.create("https://api.github.com/orgs/github/repos"),
            Mockito.mock(User.class),
            resources,
            Mockito.mock(Storage.class)
        ).iterator();
    }

}
