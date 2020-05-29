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

import com.selfxdsd.api.Contract;
import com.selfxdsd.api.Issue;
import com.selfxdsd.api.Task;
import com.selfxdsd.api.Tasks;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Unit tests for {@link ProjectTasks}.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.0.1
 */
public final class ProjectTasksTestCase {

    /**
     * ProjectTasks should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(
                Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                Mockito.mock(Task.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(3));
    }

    /**
     * Returns null when the specified Task is not found.
     */
    @Test
    public void getByIdFindsNothing() {
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.getById("123", "john/test", "github"),
            Matchers.nullValue()
        );
    }

    /**
     * Returns the found Task.
     */
    @Test
    public void getByIdFindReturnsFound() {
        final Task first = Mockito.mock(Task.class);
        final Issue issueOne = this.mockIssue(
            "123",
            "john/test",
            "github",
            Contract.Roles.DEV
        );
        Mockito.when(first.issue()).thenReturn(issueOne);
        final Task second = Mockito.mock(Task.class);
        final Issue issueTwo = this.mockIssue(
            "123",
            "john/test",
            "gitlab",
            Contract.Roles.DEV
        );
        Mockito.when(second.issue()).thenReturn(issueTwo);
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(first, second),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.getById("123", "john/test", "github"),
            Matchers.is(first)
        );
    }

    /**
     * Method ofProject should return the same instance if the ID
     * is a match.
     */
    @Test
    public void ofProjectReturnsSelfIfSameId() {
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(
                Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                Mockito.mock(Task.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.ofProject("john/test", "github"),
            Matchers.is(tasks)
        );
    }

    /**
     * Method ofProject should complain if the ID of another
     * project is given as input.
     */
    @Test(expected = IllegalStateException.class)
    public void ofProjectComplainsIfDifferentId() {
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(
                Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                Mockito.mock(Task.class)
            ),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.ofProject("john/test", "gitlab"),
            Matchers.is(tasks)
        );
    }

    /**
     * Method register works if the Issue belongs to the Project.
     */
    @Test
    public void registersNewIssue(){
        final Task registered = Mockito.mock(Task.class);
        final Issue issue = this.mockIssue(
            "123",
            "john/test",
            "github",
            Contract.Roles.DEV
        );
        Mockito.when(registered.issue()).thenReturn(issue);

        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.register(issue)).thenReturn(registered);
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(),
            storage
        );
        MatcherAssert.assertThat(tasks, Matchers.emptyIterable());
        final Task created = tasks.register(issue);
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(1));
        final Task found = tasks.getById("123", "john/test", "github");
        MatcherAssert.assertThat(created, Matchers.is(found));
    }

    /**
     * If the given Issue is not part of the Repo that the
     * project represents, registering should fail.
     */
    @Test (expected = IllegalArgumentException.class)
    public void registerComplainsOnIssueFromDiffRepo() {
        final Issue issue = this.mockIssue(
            "123",
            "john/other",
            "github",
            Contract.Roles.DEV
        );
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(),
            Mockito.mock(Storage.class)
        );
        tasks.register(issue);
    }

    /**
     * Should return tasks for contributor (name and provider).
     */
    @Test
    public void returnTasksForContributor(){
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            List.of(),
            storage
        );
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks ofContributor = Mockito.mock(Tasks.class);
        Mockito.when(all.ofContributor(
            Mockito.anyString(),
            Mockito.anyString()
        )).thenReturn(ofContributor);
        Mockito.when(storage.tasks()).thenReturn(all);

        MatcherAssert.assertThat(
            tasks.ofContributor("mihai", "github"),
            Matchers.equalTo(ofContributor)
        );
    }

    /**
     * Mock an Issue for test.
     * @param issueId ID.
     * @param repoFullName Repo fullname.
     * @param provider Provider.
     * @param role Role.
     * @return Issue.
     */
    private Issue mockIssue(
        final String issueId, final String repoFullName,
        final String provider, final String role) {
        Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn(issueId);
        Mockito.when(issue.repoFullName()).thenReturn(repoFullName);
        Mockito.when(issue.provider()).thenReturn(provider);
        Mockito.when(issue.role()).thenReturn(role);
        return issue;
    }
}
