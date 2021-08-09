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

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;

/**
 * Conversation where the PM tries to understand the
 * received command.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class Understand implements Conversation {

    /**
     * Next conversation.
     */
    private final Conversation next;

    /**
     * Ctor.
     * @param next Next in the conversation chain.
     */
    public Understand(final Conversation next) {
        this.next = next;
    }

    @Override
    public Step start(final Event event) {
        final Language language = event.project().language();
        final Comment comment = event.comment();
        final String commandType = language.categorize(comment.body());
        return this.next.start(
            new Event() {
                @Override
                public String type() {
                    return commandType;
                }

                @Override
                public Issue issue() {
                    return event.issue();
                }

                @Override
                public Comment comment() {
                    return event.comment();
                }

                @Override
                public Commit commit() {
                    return event.commit();
                }

                @Override
                public String repoNewName() {
                    return event.repoNewName();
                }

                @Override
                public Project project() {
                    return event.project();
                }
            }
        );
    }
}
