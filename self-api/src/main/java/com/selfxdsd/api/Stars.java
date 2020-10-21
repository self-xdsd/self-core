package com.selfxdsd.api;

/**
 * Repo stars.
 */
public interface Stars {

    /**
     * Star a repository for the authenticated user.
     * @return True or false, whether the starring
     *  was successful or not.
     */
    boolean star();
}