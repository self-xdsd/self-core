package com.selfxdsd.core;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.core.mock.InMemory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.net.URI;

import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link GitlabRepo}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class GitlabRepoTestCase {

    /**
     * A GitlabRepo can return its owner.
     */
    @Test
    public void returnsOwner() {
        final User owner = Mockito.mock(User.class);
        final Repo repo = new GitlabRepo(
                owner,
                URI.create("http://localhost:8080"),
                Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(repo.owner(), Matchers.is(owner));
    }

    /**
     * A GitlabRepo can be activated. The InMemory storage
     * comes with a pre-registered ProjectManager with id 1.
     *
     * @throws Exception If something goes wrong.
     */
    @Test
    public void activatesRepo() throws Exception {
        final Storage storage = new InMemory();
        MatcherAssert.assertThat(
                storage.projects(),
                Matchers.iterableWithSize(0)
        );
        MatcherAssert.assertThat(
                storage.projects().assignedTo(1),
                Matchers.iterableWithSize(0)
        );

        final User user = Mockito.mock(User.class);
        final Provider provider = Mockito.mock(Provider.class);
        when(provider.name()).thenReturn("gitlab");
        when(user.provider()).thenReturn(provider);
        final Repo repo = new GitlabRepo(
                user,
                URI.create("http://localhost:8080"),
                storage
        );
        MatcherAssert.assertThat(repo.activate(), Matchers.notNullValue());
        MatcherAssert.assertThat(
                storage.projects(),
                Matchers.iterableWithSize(1)
        );
        MatcherAssert.assertThat(
                storage.projects().assignedTo(1),
                Matchers.iterableWithSize(1)
        );
    }

}