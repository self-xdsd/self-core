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
import com.selfxdsd.api.Language;
import com.selfxdsd.api.Resignations;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversation where a Task's assignee resigns.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class Resign implements Conversation {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        Resign.class
    );

    /**
     * Next conversation, if the event type is not "resign".
     */
    private final Conversation notResign;

    /**
     * Ctor.
     * @param notResign Next in the conversation chain, if the
     *  event type is not resign.
     */
    public Resign(final Conversation notResign) {
        this.notResign = notResign;
    }

    @Override
    public Step start(final Event event) {
        final Step steps;
        if(Event.Type.RESIGN.equals(event.type())) {
            final Language language = event.project().language();
            final String author = event.comment().author();
            steps = new AuthorIsAssignee(
                new UnassignTask(
                    new RegisterResignation(
                        Resignations.Reason.ASKED,
                        new SendReply(
                            String.format(
                                language.reply("resigned.comment"),
                                author
                            ),
                            lastly -> LOG.debug("User resigned successfully.")
                        )
                    )
                ),
                new SendReply(
                    String.format(
                        language.reply("cannotResign.comment"),
                        author
                    ),
                    lastly -> LOG.debug(
                        "User is not assignee, no resignation possible."
                    )
                )
            );
        } else {
            steps = this.notResign.start(event);
        }
        return steps;
    }
}
