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
import com.selfxdsd.core.projects.English;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link AssignTaskToIssueAssignee}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.30
 * @checkstyle ExecutableStatementCount (300 lines)
 */
public final class AssignTaskToIssueAssigneeTestCase {

    /**
     * It assigns the corresponding Task to the Issue's assignee.
     */
    @Test
    public void assignsTheTask() {
        final Task assigned = Mockito.mock(Task.class);

        final Task task = Mockito.mock(Task.class);
        final Contributor contributor = Mockito.mock(Contributor.class);
        Mockito.when(task.assign(contributor)).thenReturn(assigned);

        final Labels issueLabels = Mockito.mock(Labels.class);
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");
        Mockito.when(issue.assignee()).thenReturn("mihai");
        Mockito.when(issue.comments()).thenReturn(
            Mockito.mock(Comments.class)
        );
        Mockito.when(issue.labels()).thenReturn(issueLabels);

        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(
            ofProject.getById(
                "123", "mihai/test", Provider.Names.GITHUB
            )
        ).thenReturn(task);
        final Contributors contributors = Mockito.mock(Contributors.class);
        Mockito.when(
            contributors.getById("mihai", Provider.Names.GITHUB)
        ).thenReturn(contributor);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.tasks()).thenReturn(ofProject);
        Mockito.when(project.contributors()).thenReturn(contributors);
        Mockito.when(project.language()).thenReturn(new English());

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(project);

        final Step next = Mockito.mock(Step.class);

        final Step assignTask = new AssignTaskToIssueAssignee(next);
        assignTask.perform(event);

        Mockito.verify(task, Mockito.times(1)).assign(contributor);
        Mockito.verify(
            issueLabels,
            Mockito.times(1)
        ).add("@mihai");
        Mockito.verify(next, Mockito.times(1)).perform(event);
    }

    /**
     * It should do nothing if the Issue does not have a corresponding Task.
     */
    @Test
    public void skipsMissingTask() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");
        Mockito.when(issue.assignee()).thenReturn("mihai");

        final Tasks ofProject = Mockito.mock(Tasks.class);
        Mockito.when(
            ofProject.getById(
                "123", "mihai/test", Provider.Names.GITHUB
            )
        ).thenReturn(null);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("mihai/test");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.tasks()).thenReturn(ofProject);

        final Event event = Mockito.mock(Event.class);
        Mockito.when(event.issue()).thenReturn(issue);
        Mockito.when(event.project()).thenReturn(project);

        final Step next = Mockito.mock(Step.class);
        final Step assignTask = new AssignTaskToIssueAssignee(next);

        assignTask.perform(event);

        Mockito.verify(next, Mockito.times(1)).perform(event);
    }
}
