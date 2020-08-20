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
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.pm.PreconditionCheck;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check if the comment's author is the task's assignee or not.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class AuthorIsAssignee extends PreconditionCheck {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        AuthorIsAssignee.class
    );

    /**
     * Ctor.
     * @param onTrue Step to follow if the author is the assignee.
     * @param onFalse Step to follow if the author is NOT the assignee.
     */
    public AuthorIsAssignee(final Step onTrue, final Step onFalse) {
        super(onTrue, onFalse);
    }

    @Override
    public void perform(final Event event) {
        final Project project = event.project();
        final String author = event.comment().author();
        final Task task = project.tasks().getById(
            event.issue().issueId(),
            project.repoFullName(),
            project.provider()
        );
        if(task == null || task.assignee() == null) {
            LOG.debug("There is no task or no assignee.");
            this.onFalse().perform(event);
        } else if(task.assignee().username().equals(author)) {
            LOG.debug("Author is indeed the task assignee. Resigning...");
            this.onTrue().perform(event);
        } else {
            LOG.debug(
                "Author is not the assignee, no resignation to perform."
            );
            this.onFalse().perform(event);
        }
    }
}
