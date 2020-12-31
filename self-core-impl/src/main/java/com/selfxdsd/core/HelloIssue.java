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
 * After accepting a repo Invitation, the PM should open
 * a 'no-task' Issue, introducing itself and letting the owner
 * know that setup is fully completed.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.37
 */
final class HelloIssue implements Invitation {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        HelloIssue.class
    );


    /**
     * Original Invitation.
     */
    private final Invitation origin;

    /**
     * Ctor.
     * @param origin Original Invitation.
     */
    HelloIssue(final Invitation origin) {
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
        LOG.debug("Opening 'Hello' Issue...");
        final Repo repo = this.origin.repo();
        try {
            repo.issues().open(
                "Hello from Self XDSD!",
                String.format(
                    this.helloMessage(),
                    this.inviter()
                ),
                "no-task"
            );
        } catch (final IllegalStateException ex) {
            LOG.error("Caught ISE while opening 'Hello' Issue", ex);
        }
    }

    /**
     * Get the hello message.
     * @return String.
     */
    public String helloMessage() {
        final StringBuilder message = new StringBuilder(
            "@%s Thank you for the invitation, your repo is all set up "
            + "and I will manage it starting now. \n\n"
            + "I will take care of tickets' assignment, payments and more, "
            + "automatically.\n\n"
            + "If you don't want me to handle a certain Issue or PR, add the "
            + "``no-task`` label when creating it. You can also say "
            + "``deregister`` to me (if it's already in scope) and "
            + "I will forget about it."
        );
        return message.toString();
    }
}
