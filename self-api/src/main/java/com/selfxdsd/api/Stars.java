package com.selfxdsd.api;

/**
 * Repo stars.
 * @version $Id$
 * @since 0.0.30
 */
public interface Stars {

    /**
     * Star a repository for the authenticated user.
     * @return True or false, whether the starring
     *  was successful or not.
     */
    boolean add();

    /**
     * Check if the authenticated user added the star.
     * @return True when starred or false if not or something went wrong
     * (http error like unauthenticated user etc...).
     */
    boolean added();
}
