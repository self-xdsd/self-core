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
import com.selfxdsd.api.pm.Conversation;
import com.selfxdsd.api.pm.Step;
import com.selfxdsd.core.projects.English;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link Status}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.20
 * @checkstyle ExecutableStatementCount (400 lines)
 */
public final class StatusTestCase {

    /**
     * Status replies with "task not registered" if
     * the task is not in the DB.
     */
    @Test
    public void repliesTaskNotRegistered() {
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(null);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.language()).thenReturn(new English());

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn(Event.Type.STATUS);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Conversation status = new Status(
            next -> {
                throw new IllegalStateException("Should not be called.");
            }
        );
        final Step steps = status.start(event);
        MatcherAssert.assertThat(
            steps,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SendReply.class)
            )
        );
    }

    /**
     * Status replies with "task not registered but ticket closed" if
     * the task is not in the DB and issue associated with the event is closed.
     */
    @Test
    public void repliesTaskNotRegisteredAndIssueClosed() {
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(null);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.language()).thenReturn(new English());

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(issue.isClosed()).thenReturn(Boolean.TRUE);
        Mockito.when(issue.comments()).thenReturn(Mockito.mock(Comments.class));
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");
        Mockito.when(comment.body()).thenReturn("@charlesmike status");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn(Event.Type.STATUS);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Conversation status = new Status(
            next -> {
                throw new IllegalStateException("Should not be called.");
            }
        );
        final Step steps = status.start(event);
        MatcherAssert.assertThat(
            steps,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SendReply.class)
            )
        );
        steps.perform(event);
        Mockito.verify(issue.comments()).post(
            "> @charlesmike status\n\n"
                + "@mihai this ticket is not registered as a task, "
                + "therefore I'm not working on it at the moment.\n\n"
                + "If you want me to take care of it, please reopen it."
                + " However, reopening Issues is not encouraged --"
                + "it's better if you open new tickets instead.");
    }

    /**
     * Status replies with "task not assigned" if
     * the task is present but has no assignee.
     */
    @Test
    public void repliesTaskNotAssigned() {
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(null);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(task);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.language()).thenReturn(new English());

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn(Event.Type.STATUS);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Conversation status = new Status(
            next -> {
                throw new IllegalStateException("Should not be called.");
            }
        );
        final Step steps = status.start(event);
        MatcherAssert.assertThat(
            steps,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SendReply.class)
            )
        );
    }

    /**
     * Status replies with the task's full status if
     * the task is present and has an assignee.
     */
    @Test
    public void repliesTaskStatus() {
        final Contributor assignee = Mockito.mock(Contributor.class);
        Mockito.when(assignee.username()).thenReturn("george");
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(assignee);
        final Tasks tasks = Mockito.mock(Tasks.class);
        Mockito.when(
            tasks.getById("1", "john/test", "github")
        ).thenReturn(task);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.tasks()).thenReturn(tasks);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.language()).thenReturn(new English());

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");
        final Comment comment = Mockito.mock(Comment.class);
        Mockito.when(comment.author()).thenReturn("mihai");

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn(Event.Type.STATUS);
        Mockito.when(event.comment()).thenReturn(comment);
        Mockito.when(event.project()).thenReturn(project);
        Mockito.when(event.issue()).thenReturn(issue);

        final Conversation status = new Status(
            next -> {
                throw new IllegalStateException("Should not be called.");
            }
        );
        final Step steps = status.start(event);
        MatcherAssert.assertThat(
            steps,
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(SendReply.class)
            )
        );
    }

    /**
     * Status.start(...) should call the next conversation if the
     * given Event is not 'status'.
     */
    @Test
    public void goesFurtherIfNotStatus() {
        final Step resolved = Mockito.mock(Step.class);
        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.type()).thenReturn("notStatus");
        final Conversation status = new Status(
            next -> {
                MatcherAssert.assertThat(event, Matchers.is(next));
                return resolved;
            }
        );
        MatcherAssert.assertThat(
            status.start(event),
            Matchers.is(resolved)
        );
    }

}
