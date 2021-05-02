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
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link TaskIssue}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @since 0.0.79
 * @version $Id$
 */
public final class TaskIssueTestCase {

    /**
     * TaskIssue returns the issueId from the Task.
     */
    @Test
    public void returnsIssueId() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issueId()).thenReturn("123");
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.issueId(),
            Matchers.equalTo("123")
        );
    }

    /**
     * TaskIssue returns the role from the Task.
     */
    @Test
    public void returnsRole() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.role()).thenReturn("DEV");
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.role(),
            Matchers.equalTo("DEV")
        );
    }

    /**
     * TaskIssue returns the provider from the Task.
     */
    @Test
    public void returnsProvider() {
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.provider(),
            Matchers.equalTo("github")
        );
    }

    /**
     * TaskIssue returns the repoFullName from the Task.
     */
    @Test
    public void returnsRepoFullName() {
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(task.project()).thenReturn(project);
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.repoFullName(),
            Matchers.equalTo("mihai/test")
        );
    }

    /**
     * TaskIssue returns the isPullRequest flag from the Task.
     */
    @Test
    public void returnsIsPullRequest() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.isPullRequest()).thenReturn(Boolean.TRUE);
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.isPullRequest(),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * TaskIssue returns the Estimation from the Task.
     */
    @Test
    public void returnsEstimation() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.estimation()).thenReturn(35);
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.estimation().minutes(),
            Matchers.equalTo(35)
        );
    }

    /**
     * TaskIssue is never closed. We do not store tasks for which the
     * corresponding Issues are closed, therefore TaskIssue.isClosed()
     * should always return false.
     */
    @Test
    public void isNeverClosed() {
        final Issue issue = new TaskIssue(
            Mockito.mock(Task.class),
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.isClosed(),
            Matchers.is(Boolean.FALSE)
        );
    }

    /**
     * TaskIssue.assignee() returns null if the Task is not assigned.
     */
    @Test
    public void returnsNullForMissingAssignee() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(null);
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.assignee(),
            Matchers.nullValue()
        );
    }

    /**
     * TaskIssue.assignee() returns the assignee's username
     * if the Task is  assigned.
     */
    @Test
    public void returnsAssigneeUsername() {
        final Task task = Mockito.mock(Task.class);
        final Contributor mihai = Mockito.mock(Contributor.class);
        Mockito.when(mihai.username()).thenReturn("mihai");
        Mockito.when(task.assignee()).thenReturn(mihai);
        final Issue issue = new TaskIssue(
            task,
            Mockito.mock(Repo.class)
        );
        MatcherAssert.assertThat(
            issue.assignee(),
            Matchers.equalTo("mihai")
        );
    }
}
