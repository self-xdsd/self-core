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
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Conversation where someone asks the PM about a task's status.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class Status implements Conversation {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        Status.class
    );

    /**
     * Next conversation, if the event type is not "status".
     */
    private final Conversation notStatus;

    /**
     * Ctor.
     * @param notStatus Next in the conversation chain, if the
     *  event type is not status.
     */
    public Status(final Conversation notStatus) {
        this.notStatus = notStatus;
    }

    @Override
    public Step start(final Event event) {
        final Step steps;
        if(Event.Type.STATUS.equals(event.type())) {
            final String reply;
            final Project project = event.project();
            final Language language = project.language();
            final Task task = project.tasks().getById(
                event.issue().issueId(),
                project.repoFullName(),
                project.provider()
            );
            if(task == null) {
                reply = String.format(
                    language.reply("taskNotRegistered.comment"),
                    event.comment().author()
                );
            } else if(task != null && task.assignee() == null) {
                reply = String.format(
                    language.reply("taskNotAssigned.comment"),
                    event.comment().author(),
                    task.role(),
                    task.estimation()
                );
            } else {
                reply = String.format(
                    language.reply("taskIsAssigned.comment"),
                    event.comment().author(),
                    task.assignee().username(),
                    task.assignmentDate(),
                    task.deadline(),
                    task.estimation()
                );
            }
            steps = new SendReply(
                reply,
                lastly -> LOG.debug("Task status sent successfully.")
            );
        } else {
            steps = this.notStatus.start(event);
        }
        return steps;
    }
}
