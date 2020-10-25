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
}