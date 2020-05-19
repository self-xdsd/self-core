package com.selfxdsd.core.mock;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Storage;
import com.selfxdsd.api.User;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test cases for InMemory storage.
 */
public class InMemoryTestCase {

    /**
     * Sign up a user.
     */
    @Test
    public final void userSignUp() {
        final Storage storage = new InMemory();
        final User user = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(user);

        final User signedUser = storage.users().user("amihaiemil", "GitHub");
        assertThat(signedUser.username(), equalTo("amihaiemil"));
    }

    /**
     * Sign up a user twice with the same provider.
     */
    @Test
    public final void userSignUpTwiceWithSameProvider() {
        final Storage storage = new InMemory();
        final User userGithub = mockUser("amihaiemil", "GitHub");
        final User userGithubAgain = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(userGithub);
        storage.users().signUp(userGithubAgain);

        assertThat(storage.users(), iterableWithSize(1));
    }

    /**
     * Sign up a user name with different providers.
     */
    @Test
    public final void sameUserNameSignedWithDiffProviders() {
        final Storage storage = new InMemory();
        final User userGithub = mockUser("amihaiemil", "GitHub");
        final User userBitbucket = mockUser("amihaiemil", "Bitbucket");

        storage.users().signUp(userGithub);
        storage.users().signUp(userBitbucket);

        assertThat(storage.users(), iterableWithSize(2));
    }


    /**
     * User signed up with other provider.
     * Should return null when query by a different provider.
     */
    @Test
    public final void userSignedUpWithOtherProvider() {
        final Storage storage = new InMemory();
        final User user = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(user);

        final User signedUser = storage.users().user("amihaiemil", "Bitbucket");
        assertThat(signedUser, is(nullValue()));
    }

    /**
     * Query a user before sign up. Should return null.
     */
    @Test
    public final void userNotSignedUp() {
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


    //TODO(criske): add tests cases for project managers

}