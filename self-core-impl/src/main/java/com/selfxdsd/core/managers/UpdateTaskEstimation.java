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
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.pm.Intermediary;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step which updates a Task's estimation.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.96
 */
public final class UpdateTaskEstimation extends Intermediary {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        UpdateTaskEstimation.class
    );

    /**
     * Ctor.
     * @param next The next step to perform.
     */
    public UpdateTaskEstimation(final Step next) {
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
        final int newEstimation = issue.estimation().minutes();
        task.updateEstimation(newEstimation);
        final String message = String.format(
            "The estimation of Task [#%s-%s-%s] has been updated to %s min.",
            issue.issueId(),
            issue.repoFullName(),
            issue.provider(),
            newEstimation
        );
        LOG.debug(message);
        this.next().derive(message).perform(event);
    }
}
