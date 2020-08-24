package com.selfxdsd.api.exceptions;

import com.selfxdsd.api.Repo;

/**
 * Exception thrown if we try to active an already active Repo.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class RepoAlreadyActiveException extends RuntimeException {

    /**
     * Ctor.
     * @param repo Repo in question.
     */
    public RepoAlreadyActiveException(final Repo repo){
        super("Repo \"" + repo.fullName() + "\" is already active.");
    }

}
