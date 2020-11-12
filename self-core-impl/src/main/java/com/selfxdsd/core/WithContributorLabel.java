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
package com.selfxdsd.core;

import com.selfxdsd.api.Comments;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Labels;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonObject;

/**
 * Issue decorator which adds or removes a label with the contributor's tag
 * when the issue is assigned or unassigned.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.34
 */
final class WithContributorLabel implements Issue {

    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(
        WithContributorLabel.class
    );

    /**
     * Decorated Issue.
     */
    private final Issue decorated;

    /**
     * Ctor.
     * @param decorated Decorated issue.
     */
    WithContributorLabel(final Issue decorated) {
        this.decorated = decorated;
    }

    @Override
    public String issueId() {
        return this.decorated.issueId();
    }

    @Override
    public String provider() {
        return this.decorated.provider();
    }

    @Override
    public String role() {
        return this.decorated.role();
    }

    @Override
    public String repoFullName() {
        return this.decorated.repoFullName();
    }

    @Override
    public String author() {
        return this.decorated.author();
    }

    @Override
    public String body() {
        return this.decorated.body();
    }

    @Override
    public String assignee() {
        return this.decorated.assignee();
    }

    @Override
    public boolean assign(final String username) {
        final boolean assigned = this.decorated.assign(username);
        if(assigned) {
            LOG.debug("Adding label @" + username + "... ");
            boolean labeled = this.labels().add("@" + username);
            if(labeled) {
                LOG.debug("Label added.");
            } else {
                LOG.warn("Problem while adding label.");
            }
        }
        return assigned;
    }

    @Override
    public boolean unassign(final String username) {
        final boolean unassigned = this.decorated.unassign(username);
        if(unassigned) {
            LOG.debug("Removing label @" + username + "... ");
            boolean removed = this.labels().remove("@" + username);
            if(removed) {
                LOG.debug("Label removed.");
            } else {
                LOG.warn("Problem while removing label.");
            }
        }
        return unassigned;
    }

    @Override
    public JsonObject json() {
        return this.decorated.json();
    }

    @Override
    public Comments comments() {
        return this.decorated.comments();
    }

    @Override
    public void close() {
        this.decorated.close();
    }

    @Override
    public void reopen() {
        this.decorated.reopen();
    }

    @Override
    public boolean isClosed() {
        return this.decorated.isClosed();
    }

    @Override
    public boolean isPullRequest() {
        return this.decorated.isPullRequest();
    }

    @Override
    public int estimation() {
        return this.decorated.estimation();
    }

    @Override
    public Labels labels() {
        return this.decorated.labels();
    }
}
