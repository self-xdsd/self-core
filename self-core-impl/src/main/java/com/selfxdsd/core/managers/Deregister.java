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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Event;
import com.selfxdsd.api.Language;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversation where a PM will unassign the Task and remove it from DB.
 * @author criske
 * @version $Id$
 * @since 0.0.20
 */
public final class Deregister implements Conversation {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        Deregister.class
    );

    /**
     * Next conversation, if the event type is not "deregister".
     */
    private final Conversation notDeregister;

    /**
     * Ctor.
     * @param notDeregister Next in the conversation chain, if the
     *  event type is not deregister.
     */
    public Deregister(final Conversation notDeregister) {
        this.notDeregister = notDeregister;
    }

    @Override
    public Step start(final Event event) {
        final Step steps;
        if(Event.Type.DEREGISTER.equals(event.type())) {
            final Language language = event.project().language();
            final String author = event.comment().author();
            steps = new AuthorHasRoles(
                new UnassignTask(
                    new RemoveTask(
                        new SendReply(
                            String.format(
                                language.reply("deregister.comment"),
                                author
                            )
                        )
                    )
                ),
                new SendReply(
                    String.format(
                        language.reply("cannotDeregister.comment"),
                        author
                    )
                ),
                event,
                Contract.Roles.PO, Contract.Roles.ARCH
            );
        } else {
            steps = this.notDeregister.start(event);
        }
        return steps;
    }
}
