package com.selfxdsd.core;

import com.selfxdsd.api.Login;
import com.selfxdsd.api.User;
import com.selfxdsd.api.storage.Storage;

/**
 * Base login implementation that aids concrete implementations of Login.
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
abstract class BaseLogin implements Login {

    @Override
    public User signUp(final Storage storage) {
        return signUp(storage, userCredentials(storage));
    }

    /**
     * An unauthenticated user factory that has the necessary credentials
     * for sign up.
     * @param storage Storage context that might be used by user builders.
     * @return An unauthenticated User.
     */
    protected abstract User userCredentials(final Storage storage);

    /**
     * Sign up a given user. Check if we already have him/her
     * in the Storage (from a previous authentication) and register him/her
     * if we do not.
     * @param storage Storage used to ensure authentication.
     * @param user User to sing up.
     * @return Authenticated User.
     */
    private User signUp(final Storage storage, final User user) {
        User authenticated = storage.users().user(
            user.username(), user.provider().name()
        );
        if(authenticated == null) {
            authenticated = storage.users().signUp(user);
        }
        return authenticated;
    }

}
