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

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Provider;
import com.selfxdsd.api.Repo;
import com.selfxdsd.api.pm.Intermediary;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step to invite the PM to the project's repository.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.13
 * @todo #327:15min Create classes for permissions constants in both
 *  Github and Gitlab.
 * @todo #327:30min Add method ProjectManager.userId() which should return the
 *  user ID (not username) registered with the Provider. Then, modify
 *  the invitation logic to use the user ID in case of GitLab. After this,
 *  don't forget to write unit tests for this class.
 */
public final class InvitePm extends Intermediary {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        InvitePm.class
    );

    /**
     * Ctor.
     *
     * @param next The next step to perform.
     */
    public InvitePm(final Step next) {
        super(next);
    }

    @Override
    public void perform(final Event event) {
        final Repo repo = event.project().repo();
        final String provider = event.provider();
        LOG.debug(
            "Inviting PM to repo " + repo.fullName() + " at " + provider
        );
        final String permission;
        if(Provider.Names.GITHUB.equals(provider)) {
            permission = "manage";
        } else if (Provider.Names.GITLAB.equals(provider)) {
            permission = "40";
        } else {
            throw new IllegalStateException(
                "Unknown Provider: [" + provider + "]."
            );
        }
        final boolean response = repo.collaborators().invite(
            event.project().projectManager().username(),
            permission
        );
        if(response) {
            LOG.debug("PM invited successfully!");
        } else {
            LOG.debug(
                "There was a problem while inviting the PM, "
                + "check the logs above."
            );
        }
        this.next().perform(event);
    }
}
