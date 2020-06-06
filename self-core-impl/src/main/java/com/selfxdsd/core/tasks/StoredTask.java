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
package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;

import java.time.LocalDateTime;

/**
 * A Task stored and managed by Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredTask implements Task {

    /**
     * Project where this task belongs.
     */
    private final Project project;

    /**
     * ID of the Issue that this task represents.
     */
    private final String issueId;

    /**
     * Role necessary to solve this task.
     */
    private final String role;

    /**
     * Provider.
     */
    private final String provider;

    /**
     * Contributor's username (the assignee).
     */
    private final String contributorUsername;

    /**
     * Assignment date.
     */
    private final LocalDateTime assignmentDate;

    /**
     * Deadline by when this Task should be closed.
     */
    private final LocalDateTime deadline;

    /**
     * Self Storage.
     */
    private final Storage storage;

    /**
     * Constructor for an unassigned task.
     * @param project Project.
     * @param issueId Id of the Issue that this task represents.
     * @param role Role.
     * @param provider Provider.
     * @param storage Storage.
     */
    public StoredTask(
        final Project project,
        final String issueId,
        final String role,
        final String provider,
        final Storage storage
    ) {
        this(project, issueId, role, provider, storage, null, null, null);
    }

    /**
     * Constructor for an assigned task.
     * @param project Project.
     * @param issueId Id of the Issue that this task represents.
     * @param role Role.
     * @param provider Provider.
     * @param storage Storage.
     * @param contributorUsername Contributor's username.
     * @param assignmentDate Timestamp when this task has been assigned.
     * @param deadline Deadline by when this task should be finished.
     */
    public StoredTask(
        final Project project,
        final String issueId,
        final String role,
        final String provider,
        final Storage storage,
        final String contributorUsername,
        final LocalDateTime assignmentDate,
        final LocalDateTime deadline
    ) {
        this.project = project;
        this.issueId = issueId;
        this.role = role;
        this.provider = provider;
        this.storage = storage;
        this.contributorUsername = contributorUsername;
        this.assignmentDate = assignmentDate;
        this.deadline = deadline;
    }

    @Override
    public Issue issue() {
        return this.project.repo().issues().getById(this.issueId);
    }

    @Override
    public String role() {
        return this.role;
    }

    @Override
    public Project project() {
        return this.project;
    }

    @Override
    public Contributor assignee() {
        final Contributor assignee;
        if(this.contributorUsername == null) {
            assignee = null;
        } else {
            assignee = this.project.contributors().getById(
                this.contributorUsername,
                this.provider
            );
        }
        return assignee;
    }

    @Override
    public LocalDateTime assignmentDate() {
        return this.assignmentDate;
    }

    @Override
    public LocalDateTime deadline() {
        return this.deadline;
    }
}
