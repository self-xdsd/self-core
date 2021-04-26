/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"),
 * to read the Software only. Permission is hereby NOT GRANTED to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
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
 * @since 0.0.75
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
