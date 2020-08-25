/**
 * Copyright (c) 2020, Self XDSD Contributors
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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.PreconditionCheck;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Check if the comment's author has one of the provided roles to perform
 * the next step.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class AuthorHasRoles extends PreconditionCheck {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        AuthorHasRoles.class
    );

    /**
     * Accepted roles.
     */
    private final List<String> acceptedRoles;

    /**
     * Ctor.
     * @param onTrue Step to follow if the author has on of the given roles.
     * @param onFalse Step to follow if the author none of the given roles.
     * @param roles Accepting roles.
     */
    public AuthorHasRoles(final Step onTrue,
                          final Step onFalse,
                          final String...roles) {
        super(onTrue, onFalse);
        this.acceptedRoles = Arrays.asList(roles);
    }

    @Override
    public void perform(final Event event) {
        final Project project = event.project();
        final String author = event.comment().author();
        final Contributor contributor = project
            .contributors()
            .getById(author, project.provider());
        if (contributor == null) {
            LOG.debug("Author " + author + " is not a contributor "
                + " of this project.");
            this.onFalse().perform(event);
        } else {
            boolean hasRole = false;
            if(this.acceptedRoles.contains(Contract.Roles.ANY)) {
                hasRole = true;
            } else {
                final Contracts contracts = project.contracts()
                    .ofContributor(contributor);
                for (final Contract contract : contracts) {
                    if (this.acceptedRoles.contains(contract.role())) {
                        hasRole = true;
                        break;
                    }
                }
            }
            if (hasRole) {
                LOG.debug("Author " + author + " has the right role "
                    + "the execute the next command.");
                this.onTrue().perform(event);
            } else {
                LOG.debug("Author " + author + " has no rights to "
                    + "execute the next command");
                this.onFalse().perform(event);
            }
        }
    }
}
