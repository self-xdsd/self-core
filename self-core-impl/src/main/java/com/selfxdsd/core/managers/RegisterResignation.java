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
import com.selfxdsd.api.pm.Intermediary;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step where we register a resignation for the task's assignee.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.31
 */
public final class RegisterResignation extends Intermediary {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        RegisterResignation.class
    );

    /**
     * Reason of the resignation.
     */
    private final String reason;

    /**
     * Ctor.
     * @param reason Reason of the resignation.
     * @param next The next step to perform.
     */
    public RegisterResignation(final String reason, final Step next) {
        super(next);
        this.reason = reason;
    }

    @Override
    public void perform(final Event event) {
        final String issueId = event.issue().issueId();
        final Project project = event.project();
        final Task task = project.tasks().getById(
            issueId, project.repoFullName(), project.provider()
        );
        if(task == null || task.assignee() == null) {
            LOG.debug(
                "Task #" + issueId + " is not registered or "
                + "it is not assigned, nothing to do."
            );
        } else {
            LOG.debug(
                "Registering resignation of  @" + task.assignee().username()
                + " from task #" + issueId + " of project "
                + project.repoFullName() + " at "
                + project.provider() + ". Reason: " + this.reason
            );
            task.resignations().register(task, this.reason);
            LOG.debug("Resignation registered successfully!");
        }
        this.next().perform(event);
    }
}
