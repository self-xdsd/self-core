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
 * Unit tests for {@link AuthorIsAssignee}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 * @checkstyle ExecutableStatementCount (500 lines)
 */
public final class AuthorIsAssigneeTestCase {

    /**
     * There is no task registered, so there is actually
     * no assignee.
     */
    @Test
    public void taskIsMissing(){
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github", Boolean.FALSE)
        ).thenReturn(null);
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(Mockito.mock(Comment.class));
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new AuthorIsAssignee(
            onTrue -> {
                throw new IllegalStateException(
                    "Should not be called, there is no task!"
                );
            },
            onFalse
        );
        step.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }

    /**
     * The task exists, but it has no assignee.
     */
    @Test
    public void taskHasNoAssignee(){
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(null);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github", Boolean.FALSE)
        ).thenReturn(task);
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.comment()).thenReturn(Mockito.mock(Comment.class));
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new AuthorIsAssignee(
            onTrue -> {
                throw new IllegalStateException(
                    "Should not be called, there is no assignee!"
                );
            },
            onFalse
        );
        step.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }

    /**
     * The comment's author is NOT the task's assignee.
     */
    @Test
    public void authorIsNotAssignee(){
        final Task task = Mockito.mock(Task.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("vlad");
        Mockito.when(task.assignee()).thenReturn(assignee);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github", Boolean.FALSE)
        ).thenReturn(task);
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");
        Mockito.when(event.comment()).thenReturn(comment);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onFalse = Mockito.mock(Step.class);
        final Step step = new AuthorIsAssignee(
            onTrue -> {
                throw new IllegalStateException(
                    "Should not be called, there author is not the assignee!"
                );
            },
            onFalse
        );
        step.perform(event);
        Mockito.verify(onFalse, Mockito.times(1)).perform(event);
    }

    /**
     * The comment's author is IS the task's assignee.
     */
    @Test
    public void authorIsAssignee(){
        final Task task = Mockito.mock(Task.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("mihai");
        Mockito.when(task.assignee()).thenReturn(assignee);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github", Boolean.FALSE)
        ).thenReturn(task);
        Mockito.when(project.tasks()).thenReturn(tasks);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.project()).thenReturn(project);
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");
        Mockito.when(event.comment()).thenReturn(comment);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(event.issue()).thenReturn(issue);

        final Step onTrue = Mockito.mock(Step.class);
        final Step step = new AuthorIsAssignee(
            onTrue,
            onFalse -> {
                throw new IllegalStateException(
                    "Should not be called, there author is the assignee!"
                );
            }
        );
        step.perform(event);
        Mockito.verify(onTrue, Mockito.times(1)).perform(event);
    }

}
