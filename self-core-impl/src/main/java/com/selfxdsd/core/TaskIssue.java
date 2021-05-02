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
import java.util.function.Supplier;

/**
 * Issue in a Repo, based on an existing Task.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.79
 * @version $Id$
 */
final class TaskIssue implements Issue {

    /**
     * Task registered in Self XDSD.
     */
    private final Task task;

    /**
     * Supplier of the corresponding Issue.
     */
    private final Supplier<Issue> issue;

    /**
     * Ctor.
     * @param task Task in Self XDSD.
     * @param repo Repo where this Issue exists.
     */
    TaskIssue(final Task task, final Repo repo) {
        this(
            task,
            new Supplier<>() {

                /**
                 * Cached Issue. Make sure to fetch it only once and cache
                 * the result.
                 */
                private Issue cached;

                @Override
                public Issue get() {
                    if(this.cached == null) {
                        cached = repo.issues().getById(task.issueId());
                    }
                    return cached;
                }
            }
        );
    }

    /**
     * Ctor.
     * @param task Task in Self XDSD.
     * @param issue Supplier of the corresponding Issue.
     */
    TaskIssue(final Task task, final Supplier<Issue> issue) {
        this.task = task;
        this.issue = issue;
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
        return this.issue.get().author();
    }

    @Override
    public String body() {
        return this.issue.get().author();
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
        return this.issue.get().assign(username);
    }

    @Override
    public boolean unassign(final String username) {
        return this.issue.get().unassign(username);
    }

    @Override
    public JsonObject json() {
        return this.issue.get().json();
    }

    @Override
    public Comments comments() {
        return this.issue.get().comments();
    }

    @Override
    public void close() {
        this.issue.get().close();
    }

    @Override
    public void reopen() {
        this.issue.get().reopen();
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
        return this.issue.get().labels();
    }
}
