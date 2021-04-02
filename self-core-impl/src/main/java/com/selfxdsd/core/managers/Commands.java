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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.Event;
import com.selfxdsd.api.Language;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;

/**
 * Conversation where someone asks the PM about the available PM commands.
 * @author criske
 * @version $Id$
 * @since 0.0.72
 */
public final class Commands implements Conversation {

    /**
     * Next conversation, if the event type is not "commands".
     */
    private final Conversation next;

    /**
     * Ctor.
     * @param next Next in the conversation chain, if the
     *  event type is not commands.
     */
    public Commands(final Conversation next) {
        this.next = next;
    }

    @Override
    public Step start(final Event event) {
        final Step steps;
        if(Event.Type.COMMANDS.equals(event.type())) {
            final Project project = event.project();
            final Language language = project.language();
            final String reply = language.reply("commands.comment");
            steps = new SendReply(reply);
        } else {
            steps = this.next.start(event);
        }
        return steps;
    }
}