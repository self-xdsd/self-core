package com.selfxdsd.core.mock;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.storage.Storage;
import com.selfxdsd.api.User;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for class {@link InMemoryUsers}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class InMemoryTestCase {

    /**
     * Sign up a user.
     */
    @Test
    public void userSignUp() {
        final Storage storage = new InMemory();

        storage.users().signUp(
            "amihaiemil",
            Provider.Names.GITHUB,
            "amihaiemil@gmail.com"
        );

        final User signedUser = storage.users().user(
            "amihaiemil", Provider.Names.GITHUB
        );
        assertThat(signedUser.username(), equalTo("amihaiemil"));
    }

    /**
     * Sign up a user twice with the same provider.
     */
    @Test
    public void userSignUpTwiceWithSameProvider() {
        final Storage storage = new InMemory();

        storage.users().signUp(
            "amihaiemil",
            Provider.Names.GITHUB,
            "amihaiemil@gmail.com"
        );
        storage.users().signUp(
            "amihaiemil",
            Provider.Names.GITHUB,
            "amihaiemil@gmail.com"
        );
        assertThat(storage.users(), iterableWithSize(1));
    }

    /**
     * Sign up a user name with different providers.
     */
    @Test
    public void sameUserNameSignedWithDiffProviders() {
        final Storage storage = new InMemory();

        storage.users().signUp(
            "amihaiemil",
            Provider.Names.GITHUB,
            "amihaiemil@gmail.com"
        );
        storage.users().signUp(
            "amihaiemil",
            Provider.Names.GITLAB,
            "amihaiemil@gmail.com"
        );

        assertThat(storage.users(), iterableWithSize(2));
    }


    /**
     * User signed up with other provider.
     * Should return null when query by a different provider.
     */
    @Test
    public void userSignedUpWithOtherProvider() {
        final Storage storage = new InMemory();
        final User user = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(
            "amihaiemil",
            Provider.Names.GITHUB,
            "amihaiemil@gmail.com"
        );
        final User signedUser = storage.users().user(
            "amihaiemil",
            Provider.Names.GITLAB
        );
        assertThat(signedUser, is(nullValue()));
    }

    /**
     * Query a user before sign up. Should return null.
     */
    @Test
    public void userNotSignedUp() {
        final Storage storage = new InMemory();
        final User signedUser = storage.users().user("amihaiemil", "GitHub");
        assertThat(signedUser, is(nullValue()));
    }

    /**
     * Helper method for mocking a {@link User}.
     * @param userName User name
     * @param providerName Provider name
     * @return Mocked {@link User}
     */
    @SuppressWarnings("SameParameterValue")
    private User mockUser(final String userName, final String providerName){
        final Provider provider = mock(Provider.class);
        when(provider.name()).thenReturn(providerName);

        final User user = mock(User.class);
        when(user.username()).thenReturn(userName);
        when(user.provider()).thenReturn(provider);

        return user;
    }

}
