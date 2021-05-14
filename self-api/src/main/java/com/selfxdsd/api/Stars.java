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
     * Checks if a repository is starred by the authenticated user.
     * @return True when starred or false if not or something went wrong
     * (http error like unauthenticated user etc...).
     */
    boolean isStarred();
}
