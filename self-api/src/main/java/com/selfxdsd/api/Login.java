package com.selfxdsd.api;

/**
 * Strategy interface used by {@link Self} to login into different platforms
 * (Github, Gitlab etc...).
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public interface Login {

    /**
     * User's name.
     * @return String.
     */
    String username();

    /**
     * User's email address.
     * @return String.
     */
    String email();

    /**
     * User's access token.
     * @return String.
     */
    String accessToken();

    /**
     * User's role in self (simple user, admin etc).
     * @return String.
     */
    String role();

    /**
     * Provider's name.
     * @return String.
     */
    String provider();

}
