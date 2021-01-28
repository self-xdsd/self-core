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
 * Step where we assign the corresponding Task to the Issue's assignee.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.30
 */
public final class AssignTaskToIssueAssignee extends Intermediary {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        AssignTaskToIssueAssignee.class
    );

    /**
     * Ctor.
     * @param next The next step to perform.
     */
    public AssignTaskToIssueAssignee(final Step next) {
        super(next);
    }

    @Override
    public void perform(final Event event) {
        final Issue issue = event.issue();
        final String issueId = issue.issueId();
        final Project project = event.project();
        final Task task = project.tasks().getById(
            issueId,
            project.repoFullName(),
            project.provider(),
            issue.isPullRequest()
        );
        if(task == null) {
            LOG.debug(
                "Issue #" + issueId + " in project " + project.repoFullName()
                + " at " + project.provider()
                + " is not registered as a Task, can't assign anyone!"
            );
        } else {
            final String assignee = issue.assignee();
            final Task assigned = task.assign(
                project.contributors()
                    .getById(assignee, project.provider())
            );
            LOG.debug(
                "Task #" + assigned.issueId()
                + " assigned to Contributor @" + assignee
                + " who was already assigned to the Issue."
            );
            issue.labels().add("@" + assignee);
            final String comment = String.format(
                project.language().reply("taskAssigned.comment"),
                assignee,
                assigned.deadline(),
                assigned.estimation()
            );
            issue.comments().post(comment);
        }
        this.next().perform(event);
    }
}
