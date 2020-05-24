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
     * Signs up a user using credentials given by the implementations
     * of {@link Login} (like username, access-token etc...).
     * <br>.
     * The Authentication process is delegated to a storage.
     * @param storage Storage used to sign up
     * @return An authenticated User.
     */
    User signUp(final Storage storage);

}
