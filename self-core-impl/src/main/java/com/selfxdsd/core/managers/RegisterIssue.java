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
 * Step where the event's Issue is registered as a new Task in the
 * Project.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 */
public final class RegisterIssue extends Intermediary {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        RegisterIssue.class
    );

    /**
     * Ctor.
     * @param next The next step to perform.
     */
    public RegisterIssue(final Step next) {
        super(next);
    }

    @Override
    public void perform(final Event event) {
        final Issue issue = event.issue();
        final Project project = event.project();
        LOG.debug(
            "Registering Issue #" + issue.issueId() + " as a Task in Project "
            + project.repoFullName() + " at " + project.provider() + "... "
        );
        final Task task = project.tasks().register(issue);
        if(task != null) {
            LOG.debug("Issue #" + issue.issueId() + " registered.");
        } else {
            LOG.debug(
                "Oops, something went wrong, Issue #" + issue.issueId()
                + " was not registered as a Task."
            );
        }
        super.next().perform(event);
    }
}
