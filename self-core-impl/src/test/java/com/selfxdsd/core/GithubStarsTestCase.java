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
 * Unit tests for {@link GithubStars}.
 */
public final class GithubStarsTestCase {
    /**
     * A repo can be starred ok (receives NO_CONTENT).
     */
    @Test
    public void star() {
        final Repo repo = new GithubRepo(
                Mockito.mock(JsonResources.class),
                URI.create("http://api.gitub.com/user/starred/mihai/test/"),
                Mockito.mock(User.class),
                Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
                repo.star(),
                Matchers.allOf(
                        Matchers.notNullValue(),
                        Matchers.instanceOf(GithubStars.class)
                )
        );
    }
}

