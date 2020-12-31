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
import com.selfxdsd.api.Label;
import com.selfxdsd.api.Project;
import com.selfxdsd.api.pm.PreconditionCheck;
import com.selfxdsd.api.pm.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step where we check if the Issue has the given label or not.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.30
 */
public final class IssueHasLabel extends PreconditionCheck {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        IssueHasLabel.class
    );

    /**
     * The label we're looking for.
     */
    private final String label;

    /**
     * Ctor.
     * @param label Label we're looking for.
     * @param onTrue Step to follow if the Issue has the label.
     * @param onFalse Step to follow if the Issue does NOT have the label.
     */
    public IssueHasLabel(
        final String label,
        final Step onTrue,
        final Step onFalse
    ) {
        super(onTrue, onFalse);
        this.label = label;
    }

    @Override
    public void perform(final Event event) {
        final Project project = event.project();
        final Issue issue = event.issue();
        LOG.debug(
            "Checking if Issue #" + issue.issueId()
            + " from Project " + project.repoFullName()
            + " at " + project.provider() + " has the label ["
            + this.label + "]..."
        );
        boolean hasLabel = false;
        for(final Label label : issue.labels()) {
            if(this.label.equalsIgnoreCase(label.name())) {
                hasLabel = true;
                break;
            }
        }
        if(hasLabel) {
            LOG.debug(
                "Issue #" + issue.issueId()
                + " from Project " + project.repoFullName()
                + " at " + project.provider() + " does have the label ["
                + this.label + "]!"
            );
            this.onTrue().perform(event);
        } else {
            LOG.debug(
                "Issue #" + issue.issueId()
                + " from Project " + project.repoFullName()
                + " at " + project.provider() + " does NOT have the label ["
                + this.label + "]!"
            );
            this.onFalse().perform(event);
        }
    }
}
