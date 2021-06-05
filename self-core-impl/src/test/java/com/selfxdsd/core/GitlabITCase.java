package com.selfxdsd.core;

import com.selfxdsd.api.Organizations;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import com.selfxdsd.api.exceptions.RepoException;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.MockJsonResources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;

import java.net.HttpURLConnection;

import static com.selfxdsd.core.mock.MockJsonResources.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link Gitlab}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GitlabITCase {

    /**
     * A repo can be fetched from Gitlab as json.
     */
    @Test
    public void fetchesRepoOk() {
        final Provider gitlab = createGitlab("criske");
        final JsonObject jsonRepo = gitlab.repo("criske", "test2").json();
        assertThat(jsonRepo.getInt("id"), equalTo(18889648));
        assertThat(jsonRepo.getString("path_with_namespace"),
            equalTo("criske/test2"));
    }

    /**
     * IllegalStateException is thrown because the repo is not found.
     */
    @Test(expected = RepoException.NotFound.class)
    public void fetchesRepoNotFound() {
        createGitlab("criske").repo("criske", "1231312123").json();
    }

    /**
     * Provider fetches its organizations.
     */
    @Test
    public void fetchesOrganizations() {
        final Organizations organizations = createGitlab("criske")
            .organizations();
        MatcherAssert.assertThat(organizations,
            Matchers.instanceOf(GitlabOrganizations.class));
    }

    /**
     * Gitlab.follow(...) works.
     */
    @Test
    public void followUserWorks(){
        final JsonResources res = this.createFollowResources(false, false);
        final Provider gitlab = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        final boolean followed = gitlab.follow("john");

        MatcherAssert.assertThat(followed, Matchers.is(Boolean.TRUE));
    }

    /**
     * Gitlab.follow(...) returns true if user is already followed.
     */
    @Test
    public void followAlreadyUserWorks(){
        final JsonResources res = this.createFollowResources(true, false);

        final Provider gitlab = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        final boolean followed = gitlab.follow("john");

        MatcherAssert.assertThat(followed, Matchers.is(Boolean.TRUE));
    }

    /**
     * Gitlab.follow(...) fails due to user not found.
     */
    @Test
    public void followFailsUserNotFound() {
        final JsonResources res = this.createFollowResources(true, false);

        final Provider gitlab = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        final boolean followed = gitlab.follow("not-john");

        MatcherAssert.assertThat(followed, Matchers.is(Boolean.FALSE));
    }

    /**
     * Gitlab.follow(...) returns false if user is null.
     */
    @Test
    public void followFailsUserIsNull() {
        final JsonResources res = this.createFollowResources(false, false);

        final Provider gitlab = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        final boolean followed = gitlab.follow(null);

        MatcherAssert.assertThat(followed, Matchers.is(Boolean.FALSE));
    }

    /**
     * Gitlab.follow(...) returns false if user is empty.
     */
    @Test
    public void followFailsUserIsEmpty() {
        final JsonResources res = this.createFollowResources(false, false);

        final Provider gitlab = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        final boolean followed = gitlab.follow("   ");

        MatcherAssert.assertThat(followed, Matchers.is(Boolean.FALSE));
    }

    /**
     * Gitlab.follow(...) returns false if request failing due to server issues,
     * rate-limited, unauthorized etc..
     */
    @Test
    public void followFailsDueToOtherError() {
        final JsonResources res = this.createFollowResources(false, true);

        final Provider gitlab = new Gitlab(
            Mockito.mock(User.class),
            Mockito.mock(Storage.class),
            res
        );

        final boolean followed = gitlab.follow("john");

        MatcherAssert.assertThat(followed, Matchers.is(Boolean.FALSE));
    }

    /**
     * Creates a "follow" resources requests context.
     * @param alreadyFollowed Is already following?
     * @param failOnFollow Fails when follow (server issues, rate limit etc.)
     * @return MockJsonResources.
     */
    private JsonResources createFollowResources(
        final boolean alreadyFollowed,
        final boolean failOnFollow
    ){
        return new MockJsonResources(req -> {
            final MockResource mock;
            final String method = req.getMethod();
            if ("GET".equals(method)) {
                if (req.getUri().toString()
                    .endsWith("/users?username=john")) {
                    mock = new MockResource(
                        HttpURLConnection.HTTP_OK,
                        Json.createArrayBuilder()
                            .add(
                                Json.createObjectBuilder()
                                    .add("id", 1)
                                    .build()
                            )
                            .build()

                    );
                } else {
                    mock = new MockResource(
                        HttpURLConnection.HTTP_OK,
                        JsonValue.EMPTY_JSON_ARRAY
                    );
                }
            } else if ("POST".equals(method)) {
                if (req.getUri().toString()
                    .endsWith("/users/1/follow")) {
                    final int okStatus;
                    if(!failOnFollow) {
                        if (alreadyFollowed) {
                            okStatus = HttpURLConnection.HTTP_NOT_MODIFIED;
                        } else {
                            okStatus = HttpURLConnection.HTTP_CREATED;
                        }
                    }else{
                        okStatus = HttpURLConnection.HTTP_UNAVAILABLE;
                    }
                    mock = new MockResource(
                        okStatus,
                        JsonValue.NULL
                    );
                } else {
                    mock = new MockResource(
                        HttpURLConnection.HTTP_NOT_FOUND,
                        JsonValue.NULL
                    );
                }
            } else {
                mock = new MockResource(
                    HttpURLConnection.HTTP_BAD_METHOD,
                    JsonValue.NULL
                );
            }
            return mock;
        });
    }

    /**
     * Gitlab provider exposes current authenticated user personal repos.
     */
    @Test
    public void hasPersonalRepos() {
        MatcherAssert.assertThat(
            new Gitlab(
                Mockito.mock(User.class),
                Mockito.mock(Storage.class)
            ).repos(),
            Matchers.instanceOf(GitlabPersonalRepos.class)
        );
    }

    /**
     * Creates a Gitlab instance based on a mocked
     * {@link com.selfxdsd.api.User}.
     *
     * @param username Provided user name
     * @return Created Gitlab instance
     */
    private Gitlab createGitlab(final String username) {
        final User user = mock(User.class);
        when(user.username()).thenReturn(username);
        when(user.provider()).then(invocation -> new Gitlab(
            (User) invocation.getMock(),
            null,
            new ConditionalJsonResources(new JdkHttp())
        ));
        return (Gitlab) user.provider();
    }

}
