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
package com.selfxdsd.core;

import com.selfxdsd.api.*;

import javax.json.JsonObject;

/**
 * Issue in a Repo, based on an existing Task.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.79
 * @version $Id$
 */
final class TaskIssue implements Issue {

    /**
     * Repo where this Issue exists.
     */
    private final Repo repo;

    /**
     * Task registered in Self XDSD.
     */
    private final Task task;

    /**
     * Ctor.
     * @param repo Repo where this Issue exists.
     * @param task Task in Self XDSD.
     */
    public TaskIssue(final Repo repo, final Task task) {
        this.repo = repo;
        this.task = task;
    }

    @Override
    public String issueId() {
        return this.task.issueId();
    }

    @Override
    public String provider() {
        return this.task.project().provider();
    }

    @Override
    public String role() {
        return this.task.role();
    }

    @Override
    public String repoFullName() {
        return this.task.project().repoFullName();
    }

    @Override
    public String author() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public String body() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public String assignee() {
        final String assignee;
        if(this.task.assignee() != null) {
            assignee = this.task.assignee().username();
        } else {
            assignee = null;
        }
        return assignee;
    }

    @Override
    public boolean assign(final String username) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean unassign(final String username) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public JsonObject json() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public Comments comments() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public void reopen() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isPullRequest() {
        return this.task.isPullRequest();
    }

    @Override
    public Estimation estimation() {
        return () -> TaskIssue.this.task.estimation();
    }

    @Override
    public Labels labels() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}
