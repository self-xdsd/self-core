package com.selfxdsd.core;

import com.selfxdsd.api.ApiToken;
import com.selfxdsd.api.User;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link StoredApiTokens}.
 */
public final class StoredApiTokensTestCase {

    /**
     * Throws if unable to find User.
     */
    @Test(expected = NoSuchElementException.class)
    public void throwsWhenNoSuchUser() {
        new StoredApiTokens(
            Map.of()
        ).ofUser(
            Mockito.mock(User.class)
        );
    }

    /**
     * Can find specific user's tokens.
     */
    @Test
    public void findsByUser() {
        final User user = Mockito.mock(User.class);
        final ApiToken token = Mockito.mock(ApiToken.class);
        MatcherAssert.assertThat(
            new StoredApiTokens(
                Map.of(user, List.of(token))
            ).ofUser(user),
            Matchers.hasItem(token)
        );
    }

    /**
     * Tokens can be iterated over.
     */
    @Test
    public void iteratesOverAllTokens() {
        MatcherAssert.assertThat(
            new StoredApiTokens(
                Map.of(
                    Mockito.mock(User.class),
                    List.of(Mockito.mock(ApiToken.class)),
                    Mockito.mock(User.class),
                    List.of(Mockito.mock(ApiToken.class)),
                    Mockito.mock(User.class),
                    List.of(Mockito.mock(ApiToken.class))
                )
            ),
            Matchers.iterableWithSize(3)
        );
    }
}