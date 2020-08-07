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

/**
 * Unit tests for {@link GitlabOrganizationRepos}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.9
 */
public final class GitlabOrganizationReposTestCase {

    /**
     * Fetches organization repos data where User has admin rights.
     */
    @Test
    public void fetchesAndFilterOk() {
        final JsonResources resources = new MockJsonResources(
            r -> {
                MatcherAssert.assertThat(r.getUri().toString(),
                    Matchers.equalTo("https://gitlab.com/api/v4/groups"
                        + "/1/projects?simple=true&owned=true"));
                return new MockResource(200, Json
                    .createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1)
                        .add("path", "gitlab-repo-1")
                        .add("_links", Json
                            .createObjectBuilder()
                            .add("self", "https://gitlab.com/api/v4/projects/1")
                            .build())
                        .build())
                    .add(Json.createObjectBuilder()
                        .add("id", 2)
                        .add("path", "gitlab-repo-2")
                        .add("_links", Json
                            .createObjectBuilder()
                            .add("self", "https://gitlab.com/api/v4/projects/2")
                            .build())
                        .build())
                    .add(Json.createObjectBuilder()
                        .add("id", 3)
                        .add("path", "gitlab-repo-3")
                        .add("_links", Json
                            .createObjectBuilder()
                            .add("self", "https://gitlab.com/api/v4/projects/3")
                            .build())
                        .build())
                    .build());
            }
        );
        final Repos repos = () -> new GitlabOrganizationRepos(
            "1",
            Mockito.mock(User.class),
            resources,
            Mockito.mock(Storage.class)
        ).iterator();

        MatcherAssert.assertThat(repos, Matchers.iterableWithSize(3));

    }

    /**
     * Throw if organization repos are not fetched.
     */
    @Test(expected = IllegalStateException.class)
    public void fetchesNotOk() {
        final JsonResources resources = new MockJsonResources(
            r -> new MockResource(404, JsonValue.NULL)
        );
        new GitlabOrganizationRepos(
            "1",
            Mockito.mock(User.class),
            resources,
            Mockito.mock(Storage.class)
        ).iterator();
    }

}
