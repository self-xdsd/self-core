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
package com.selfxdsd.core.projects;

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Provider;

/**
 * Webhook events factory. This class is used by self-pm and self-todos,
 * to create an Event from the payload they receive from Github/GitLab.
 *
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.61
 */
public final class WebhookEvents {

    /**
     * Hidden ctor.
     */
    private WebhookEvents(){}

    /**
     * Create the appropriate webhook event.
     * @param project Project where the event took place.
     * @param type Type of the event.
     * @param payload Payload in JSON.
     * @return Event.
     */
    public static Event create(
        final Project project,
        final String type,
        final String payload
    ){
        final String provider = project.provider();
        final Event event;
        if(Provider.Names.GITHUB.equalsIgnoreCase(provider)) {
            event = new GithubWebhookEvent(
                project,
                type,
                payload
            );
        } else if(Provider.Names.GITLAB.equalsIgnoreCase(provider)) {
            event = new GitlabWebhookEvent(
                project,
                type,
                payload
            );
        } else {
            throw new IllegalStateException(
                "Provider " + provider + " not yet implemented. "
                + "Cannot build a webhook event for it."
            );
        }
        return event;
    }
}
