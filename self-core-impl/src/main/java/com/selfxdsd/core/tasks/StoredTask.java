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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A Task stored and managed by Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredTask implements Task {

    /**
     * Contract to which this task belongs.
     */
    private final Contract contract;

    /**
     * ID of the Issue that this task represents.
     */
    private final String issueId;

    /**
     * Assignment date.
     */
    private final LocalDateTime assignmentDate;

    /**
     * Deadline by when this Task should be closed.
     */
    private final LocalDateTime deadline;

    /**
     * Estimation in minutes.
     */
    private final int estimation;

    /**
     * Self Storage.
     */
    private final Storage storage;

    /**
     * Constructor for an unassigned task.
     * @param project Project.
     * @param issueId Id of the Issue that this task represents.
     * @param role Role.
     * @param storage Storage.
     */
    public StoredTask(
        final Project project,
        final String issueId,
        final String role,
        final Storage storage
    ) {
        this(
            new Unassigned(project, role),
            issueId,
            storage,
            null,
            null,
            0
        );
    }

    /**
     * Constructor for an assigned task.
     * @param contract Contract to which this task is assigned.
     * @param issueId Id of the Issue that this task represents.
     * @param storage Storage.
     * @param assignmentDate Timestamp when this task has been assigned.
     * @param deadline Deadline by when this task should be finished.
     * @param estimation Estimation in minutes.
     */
    public StoredTask(
        final Contract contract,
        final String issueId,
        final Storage storage,
        final LocalDateTime assignmentDate,
        final LocalDateTime deadline,
        final int estimation
    ) {
        this.contract = contract;
        this.issueId = issueId;
        this.storage = storage;
        this.assignmentDate = assignmentDate;
        this.deadline = deadline;
        this.estimation = estimation;
    }

    @Override
    public Issue issue() {
        return this.contract
            .project()
            .repo()
            .issues()
            .getById(this.issueId);
    }

    @Override
    public String role() {
        return this.contract.role();
    }

    @Override
    public Project project() {
        return this.contract.project();
    }

    @Override
    public Contributor assignee() {
        return this.contract.contributor();
    }

    @Override
    public LocalDateTime assignmentDate() {
        return this.assignmentDate;
    }

    @Override
    public LocalDateTime deadline() {
        return this.deadline;
    }

    @Override
    public BigDecimal value() {
        return this.contract.hourlyRate().multiply(
            BigDecimal.valueOf(this.estimation)
        ).divide(
            BigDecimal.valueOf(60),
            RoundingMode.HALF_UP
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.issueId,
            this.contract.project().repoFullName(),
            this.contract.project().provider());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Task)) {
            return false;
        }
        final Task other = (Task) obj;
        final Project otherProject = other.project();
        return this.issueId.equals(other.issue().issueId())
            && this.project().repoFullName().equals(otherProject.repoFullName())
            && this.project().provider().equals(otherProject.provider());
    }

    /**
     * Unassigned contract used when creating a StoredTask which
     * is not assigned to anyone.
     * @author Mihai Andronache (amihaiemil@gmail.com)
     * @version $Id$
     * @since 0.0.4
     */
    private static final class Unassigned implements Contract {

        /**
         * Project.
         */
        private final Project project;

        /**
         * Role (DEV, REV  etc).
         */
        private final String role;

        /**
         * Constructor.
         * @param project Project.
         * @param role Role.
         */
        Unassigned(final Project project, final String role) {
            this.project = project;
            this.role = role;
        }

        @Override
        public Project project() {
            return this.project;
        }

        @Override
        public Contributor contributor() {
            return null;
        }

        @Override
        public BigDecimal hourlyRate() {
            return BigDecimal.valueOf(0);
        }

        @Override
        public String role() {
            return this.role;
        }

        @Override
        public Invoices invoices() {
            return null;
        }

        @Override
        public Tasks tasks() {
            return null;
        }
    }
}
