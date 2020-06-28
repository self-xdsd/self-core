package com.selfxdsd.core;

import com.selfxdsd.api.Repo;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;
import java.net.URI;

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
            Mockito.mock(JsonResources.class),
            URI.create("http://localhost:8080"),
            owner,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(repo.owner(), Matchers.is(owner));
    }

}
