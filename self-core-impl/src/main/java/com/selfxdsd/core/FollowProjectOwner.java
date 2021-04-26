package com.selfxdsd.core;

import com.selfxdsd.api.Invitation;
import com.selfxdsd.api.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;

/**
 * Follow the PO after accepting the Invitation.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.37
 */
final class FollowProjectOwner implements Invitation {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        FollowProjectOwner.class
    );


    /**
     * Original Invitation.
     */
    private final Invitation origin;

    /**
     * Ctor.
     * @param origin Original Invitation.
     */
    FollowProjectOwner(final Invitation origin) {
        this.origin = origin;
    }

    @Override
    public JsonObject json() {
        return this.origin.json();
    }

    @Override
    public String inviter() {
        return this.origin.inviter();
    }

    @Override
    public Repo repo() {
        return this.origin.repo();
    }

    @Override
    public void accept() {
        this.origin.accept();
        LOG.debug("Following PO...");
        try {
            this.repo().owner().provider().follow(this.inviter());
        } catch (final IllegalStateException ex) {
            LOG.error("Caught ISE while following PO", ex);
        }
    }
}
