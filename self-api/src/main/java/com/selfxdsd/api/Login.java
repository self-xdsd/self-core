package com.selfxdsd.api;

import com.selfxdsd.api.storage.Storage;

/**
 * Strategy interface used by {@link Self} to login into different platforms
 * (Github, Gitlab etc...).
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public interface Login {

    /**
     * Signs up a user created by the implementations of {@link Login}
     * <br>.
     * The Authentication process is delegated to a storage.
     * @param storage Storage used to sign up.
     * @return An authenticated User.
     */
    default User signUp(final Storage storage) {
        User user = user(storage);
        User authenticated = storage.users().user(
            user.username(), user.provider().name()
        );
        if (authenticated == null) {
            authenticated = storage.users().signUp(user);
        }
        return authenticated;
    }

    /**
     * User created by implementations of {@link Login}.
     * @param storage Storage that might be used to create the user.
     * @return User
     */
    User user(final Storage storage);

}
