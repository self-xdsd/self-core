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
package com.selfxdsd.api;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A Task managed by Self.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public interface Task {

    /**
     * Issue ID.
     * @return String.
     */
    String issueId();

    /**
     * Role that should take this Task.
     * @return String.
     */
    String role();

    /**
     * Issue representing this Task on Github/Gitlab/Bitbucket etc.
     * @return Issue.
     */
    Issue issue();

    /**
     * Project where this Task belongs.
     * @return Project.
     */
    Project project();

    /**
     * Contributor to whom this Task is assigned.
     * @return Contributor.
     */
    Contributor assignee();

    /**
     * Get the Contract.
     * @return Contract.
     */
    Contract contract();

    /**
     * Assign this Task to the given Contributor.
     * @param contributor Contributor.
     * @return The assigned task.
     */
    Task assign(final Contributor contributor);

    /**
     * Unassign this Task.
     * @return The unassigned task.
     */
    Task unassign();

    /**
     * Resignations from this Task.
     * @return Resignations
     */
    Resignations resignations();

    /**
     * When was this Task assigned?
     * @return LocalDateTime.
     */
    LocalDateTime assignmentDate();

    /**
     * Deadline of this Task.
     * @return LocalDateTime.
     */
    LocalDateTime deadline();

    /**
     * Value of this Task in cents.
     * @return BigDecimal.
     */
    BigDecimal value();

    /**
     * Estimation in minutes.
     * @return Integer estimation in minutes.
     */
    int estimation();

    /**
     * Updates this task with a new estimation.
     * @param estimation Estimation in minutes.
     * @return Updated task.
     */
    Task updateEstimation(final int estimation);

    /**
     * Flags if this task is a PR.
     * @return Boolean.
     */
    boolean isPullRequest();
}
