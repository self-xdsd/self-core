package com.selfxdsd.core;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.User;
import org.junit.Test;

import javax.json.JsonObject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration tests for {@link Gitlab}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GitlabTestCase {

    /**
     * A repo can be fetched from Gitlab as json.
     */
    @Test
    public void fetchesRepoOk() {
        final Provider gitlab = createGitlab("criske");
        final JsonObject jsonRepo = gitlab.repo("18889648").json();
        assertThat(jsonRepo.getInt("id"), equalTo(18889648));
        assertThat(jsonRepo.getString("path_with_namespace"),
                equalTo("criske/test2"));
    }

    /**
     * IllegalStateException is thrown because the repo is not found.
     */
    @Test(expected = IllegalStateException.class)
    public void fetchesRepoNotFound() {
        createGitlab("criske").repo("1231312123").json();
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
                null));
        return (Gitlab) user.provider();
    }

}
