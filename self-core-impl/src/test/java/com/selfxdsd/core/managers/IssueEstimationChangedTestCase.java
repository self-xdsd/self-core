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

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Step;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link IssueEstimationChanged}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.96
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class IssueEstimationChangedTestCase {

    /**
     * IssueEstimationChange calls onTrue because the Issue's estimation
     * differs from the Task's estimation.
     */
    @Test
    public void issueEstimationDidChange() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.estimation()).thenReturn(30);

        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Estimation estimation = Mockito.mock(Estimation.class);
        Mockito.when(estimation.minutes()).thenReturn(60);
        Mockito.when(issue.estimation()).thenReturn(estimation);
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github", Boolean.FALSE)
        ).thenReturn(task);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(event.project()).thenReturn(project);

        final Step onTrue = Mockito.mock(Step.class);
        final Step step = new IssueEstimationChanged(
            onTrue,
            (onFalse) -> {
                throw new IllegalStateException("Should call the onTrue step.");
            }
        );
        step.perform(event);
        Mockito.verify(task, Mockito.times(1)).estimation();
        Mockito.verify(issue, Mockito.times(1)).estimation();
        Mockito.verify(estimation, Mockito.times(1)).minutes();
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

    /**
     * IssueEstimationChange calls onFalse because the Issue's estimation
     * does NOT differ from the Task's estimation.
     */
    @Test
    public void issueEstimationDidNotChange() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.estimation()).thenReturn(30);

        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Estimation estimation = Mockito.mock(Estimation.class);
        Mockito.when(estimation.minutes()).thenReturn(30);
        Mockito.when(issue.estimation()).thenReturn(estimation);
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github", Boolean.FALSE)
        ).thenReturn(task);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(event.project()).thenReturn(project);

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new IssueEstimationChanged(
            (onTrue) -> {
                throw new IllegalStateException(
                    "Should call the onFalse step."
                );
            },
            onFalse
        );
        step.perform(event);
        Mockito.verify(task, Mockito.times(1)).estimation();
        Mockito.verify(issue, Mockito.times(1)).estimation();
        Mockito.verify(estimation, Mockito.times(1)).minutes();
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }
}
