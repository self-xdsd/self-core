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
import com.selfxdsd.core.projects.StoredProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

/**
 * Unit tests for {@link StoredTask}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class StoredTaskTestCase {

    /**
     * StoredTask can return its Project.
     */
    @Test
    public void returnsProject() {
        final Project project = Mockito.mock(Project.class);
        final Task task = new StoredTask(
            project,
            "issueId123",
            Contract.Roles.DEV,
            "github",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(task.project(), Matchers.is(project));
    }

    /**
     * StoredTask can return its Issue.
     */
    @Test
    public void returnsIssue() {
        final Issue issue = Mockito.mock(Issue.class);
        final Issues all = Mockito.mock(Issues.class);
        Mockito.when(all.getById("123")).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.issues()).thenReturn(all);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repo()).thenReturn(repo);

        final Task task = new StoredTask(
            project,
            "123",
            Contract.Roles.DEV,
            "github",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(task.issue(), Matchers.is(issue));
    }

    /**
     * StoredTask can return the role.
     */
    @Test
    public void returnsRole() {
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            "github",
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * StoredTask can return its assignment date.
     */
    @Test
    public void returnsAssignmentDate() {
        final LocalDateTime assignment = LocalDateTime.now();
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            "github",
            Mockito.mock(Storage.class),
            "mihai",
            assignment,
            assignment.plusDays(10)
        );
        MatcherAssert.assertThat(
            task.assignmentDate().isEqual(assignment),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * StoredTask can return its deadline date.
     */
    @Test
    public void returnsDeadline() {
        final LocalDateTime assignment = LocalDateTime.now();
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            "github",
            Mockito.mock(Storage.class),
            "mihai",
            assignment,
            assignment.plusDays(10)
        );
        MatcherAssert.assertThat(
            task.deadline().isEqual(assignment.plusDays(10)),
            Matchers.is(Boolean.TRUE)
        );
    }

    /**
     * Returns the assignee Contributor.
     */
    @Test
    public void returnsAssignee() {
        final Contributor mihai = Mockito.mock(Contributor.class);

        final Contributors all = Mockito.mock(Contributors.class);
        final Contributors ofProject = Mockito.mock(Contributors.class);
        Mockito.when(
            all.ofProject("john/test", Provider.Names.GITHUB)
        ).thenReturn(ofProject);
        Mockito.when(
            ofProject.getById("mihai", Provider.Names.GITHUB)
        ).thenReturn(mihai);

        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.contributors()).thenReturn(all);

        final Provider prov = Mockito.mock(Provider.class);
        Mockito.when(prov.name()).thenReturn(Provider.Names.GITHUB);
        final User owner = Mockito.mock(User.class);
        Mockito.when(owner.provider()).thenReturn(prov);

        final Project project = new StoredProject(
            owner,
            "john/test",
            Mockito.mock(ProjectManager.class),
            storage
        );

        final Task task = new StoredTask(
            project,
            "issueId123",
            Contract.Roles.DEV,
            "github",
            Mockito.mock(Storage.class),
            "mihai",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10)
        );
        MatcherAssert.assertThat(task.assignee(), Matchers.is(mihai));
    }
}
