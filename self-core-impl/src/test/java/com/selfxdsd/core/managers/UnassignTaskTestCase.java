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
package com.selfxdsd.core.managers;

import com.selfxdsd.api.*;
import com.selfxdsd.api.pm.Step;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link UnassignTask}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class UnassignTaskTestCase {

    /**
     * There is actually no task registered for the
     * event's issue.
     */
    @Test
    public void taskIsMissing() {
        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(null);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(event.project()).thenReturn(project);

        final Step next = Mockito.mock(Step.class);
        final Step unassign = new UnassignTask(next);
        unassign.perform(event);
        Mockito.verify(next, Mockito.times(1)).perform(event);
    }

    /**
     * There is a Task registered for the event's Issue, but it is not
     * assigned to anyone.
     */
    @Test
    public void taskIsNotAssigned() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(null);
        Mockito.when(task.unassign()).thenThrow(
            new IllegalStateException("Task.unassign() should not be called!")
        );

        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(task);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(event.project()).thenReturn(project);

        final Step next = Mockito.mock(Step.class);
        final Step unassign = new UnassignTask(next);
        unassign.perform(event);
        Mockito.verify(next, Mockito.times(1)).perform(event);
    }

    /**
     * The Task corresponding to the event's Issue is unassigned.
     */
    @Test
    public void taskIsUnassigned() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(
            Mockito.mock(Contributor.class)
        );
        Mockito.when(task.unassign()).thenReturn(Mockito.mock(Task.class));

        final Event event = Mockito.mock(Event.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(task);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(event.project()).thenReturn(project);

        Mockito.when(task.resignations())
            .thenReturn(Mockito.mock(Resignations.class));

        final Step next = Mockito.mock(Step.class);
        final Step unassign = new UnassignTask(next);
        unassign.perform(event);

        Mockito.verify(task, Mockito.times(1)).unassign();
        Mockito.verify(task.resignations(), Mockito.times(1))
            .register(task, Resignations.Reason.ASKED);
        Mockito.verify(next, Mockito.times(1)).perform(event);
    }

}
