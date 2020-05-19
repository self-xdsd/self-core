package com.selfxdsd.core.mock;

import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Storage;
import com.selfxdsd.api.User;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/*
 * Unit tests for class {@link InMemoryUsers}.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 * @todo #16:30min Continue writing unit tests for the InMemory storage infrastructure.
 *  In the end all classes and methods should be covered with unit tests so we can rely 100%
 *  on them.
 */
public class InMemoryTestCase {

    //users test cases
    @Test
    public void userSignUp() {
        final Storage storage = new InMemory();
        final User user = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(user);

        final User signedUser = storage.users().user("amihaiemil", "GitHub");
        assertThat(signedUser.username(), equalTo("amihaiemil"));
    }

    @Test
    public void userSignUpTwiceWithSameProvider() {
        final Storage storage = new InMemory();
        final User user1 = mockUser("amihaiemil", "GitHub");
        final User user2 = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(user1);
        storage.users().signUp(user2);

        assertThat(storage.users(), iterableWithSize(1));
    }

    @Test
    public void sameUserNameSignedWithDiffProviders() {
        final Storage storage = new InMemory();
        final User user1 = mockUser("amihaiemil", "GitHub");
        final User user2 = mockUser("amihaiemil", "Bitbucket");

        storage.users().signUp(user1);
        storage.users().signUp(user2);

        assertThat(storage.users(), iterableWithSize(2));
    }


    @Test
    public void userSignedUpWithOtherProvider() {
        final Storage storage = new InMemory();
        final User user = mockUser("amihaiemil", "GitHub");

        storage.users().signUp(user);

        final User signedUser = storage.users().user("amihaiemil", "Bitbucket");
        assertThat(signedUser, is(nullValue()));
    }

    @Test
    public void userNotSignedUp() {
        final Storage storage = new InMemory();
        final User signedUser = storage.users().user("amihaiemil", "GitHub");
        assertThat(signedUser, is(nullValue()));
    }


    @SuppressWarnings("SameParameterValue")
    private User mockUser(String userName, String providerName){
        final Provider provider = mock(Provider.class);
        when(provider.name()).thenReturn(providerName);

        final User user = mock(User.class);
        when(user.username()).thenReturn(userName);
        when(user.provider()).thenReturn(provider);

        return user;
    }
}
