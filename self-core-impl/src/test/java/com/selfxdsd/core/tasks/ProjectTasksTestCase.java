/**
 * Copyright (c) 2020, Self XDSD Contributors
 * All rights reserved.
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permission is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.Contract.Roles;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Unit tests for {@link ProjectTasks}.
 *
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
            () -> Stream.of(
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
            Stream::empty,
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
            () -> Stream.of(first, second),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.getById("123", "john/test", "github"),
            Matchers.is(first)
        );
    }

    /**
     * Method ofProject should return the same instance if the ID is a match.
     */
    @Test
    public void ofProjectReturnsSelfIfSameId() {
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            () -> Stream.of(
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
     * Method ofProject should complain if the ID of another project is given as
     * input.
     */
    @Test(expected = IllegalStateException.class)
    public void ofProjectComplainsIfDifferentId() {
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            () -> Stream.of(
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
    public void registersNewIssue() {
        final Task registered = Mockito.mock(Task.class);
        final Issue issue = this.mockIssue(
            "123",
            "john/test",
            "github",
            Contract.Roles.DEV
        );
        Mockito.when(registered.issue()).thenReturn(issue);

        final List<Task> source = new ArrayList<>();
        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.spliterator()).thenAnswer(inv -> source.spliterator());
        Mockito.when(all.register(issue)).thenAnswer(inv -> {
            source.add(registered);
            return registered;
        });
        final Storage storage = Mockito.mock(Storage.class);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            ()-> StreamSupport.stream(all.spliterator(), false),
            storage
        );
        MatcherAssert.assertThat(tasks, Matchers.emptyIterable());
        final Task created = tasks.register(issue);
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(1));
        final Task found = tasks.getById("123", "john/test", "github");
        MatcherAssert.assertThat(created, Matchers.is(found));
    }

    /**
     * If the given Issue is not part of the Repo that the project represents,
     * registering should fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void registerComplainsOnIssueFromDiffRepo() {
        final Issue issue = this.mockIssue(
            "123",
            "john/other",
            "github",
            Contract.Roles.DEV
        );
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        tasks.register(issue);
    }

    /**
     * Should return tasks for contributor (name and provider).
     */
    @Test
    public void returnTasksForContributor() {
        final Storage storage = Mockito.mock(Storage.class);

        final Contributor mihai = Mockito.mock(Contributor.class);
        Mockito.when(mihai.username()).thenReturn("mihai");
        Mockito.when(mihai.provider()).thenReturn(Provider.Names.GITHUB);
        final Task mihaiTaskOne = Mockito.mock(Task.class);
        Mockito.when(mihaiTaskOne.assignee()).thenReturn(mihai);
        final Task mihaiTaskTwo = Mockito.mock(Task.class);
        Mockito.when(mihaiTaskTwo.assignee()).thenReturn(mihai);

        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            () -> Stream.of(
                Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                mihaiTaskOne,
                mihaiTaskTwo
            ),
            storage
        );
        MatcherAssert.assertThat(
            tasks.ofContributor("mihai", Provider.Names.GITHUB),
            Matchers.iterableWithSize(2)
        );
    }

    /**
     * Should return tasks for contract.
     */
    @Test
    public void returnTasksForContract() {
        final Contract.Id contractId = new Contract.Id("foo", "mihai",
            Provider.Names.GITHUB, Roles.DEV);

        final Contributor mihai = Mockito.mock(Contributor.class);
        Mockito.when(mihai.username()).thenReturn("mihai");
        Mockito.when(mihai.provider()).thenReturn(Provider.Names.GITHUB);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.repoFullName()).thenReturn("foo");
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(mihai);
        Mockito.when(task.role()).thenReturn(Roles.DEV);
        Mockito.when(task.project()).thenReturn(project);

        final Storage storage = Mockito.mock(Storage.class);

        final Tasks tasks = new ProjectTasks(
            "foo", Provider.Names.GITHUB,
            () -> Stream.of(task),
            storage
        );
        MatcherAssert.assertThat(tasks.ofContract(contractId),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * Returns unassigned tasks of the project.
     */
    @Test
    public void returnsUnassignedTasks() {
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.repoFullName()).thenReturn("john/test");

        final Task assigned = Mockito.mock(Task.class);
        Mockito.when(assigned.assignee())
            .thenReturn(Mockito.mock(Contributor.class));
        Mockito.when(assigned.project()).thenReturn(project);

        final Task unassigned = Mockito.mock(Task.class);
        Mockito.when(unassigned.project()).thenReturn(project);

        final Tasks tasks = new ProjectTasks(
            "john/test", Provider.Names.GITHUB,
            () -> Stream.of(assigned, unassigned),
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(tasks.unassigned(),
            Matchers.iterableWithSize(1));
    }

    /**
     * Method assign(Task, Contract, days) should be delegated to the
     * storage Tasks.
     */
    @Test
    public void assignsTaskToContract() {
        final Storage storage = Mockito.mock(Storage.class);
        final Task task = Mockito.mock(Task.class);
        final Contract contract = Mockito.mock(Contract.class);
        final int days = 10;

        final Task assigned = Mockito.mock(Task.class);
        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.assign(task, contract, days)).thenReturn(assigned);

        Mockito.when(storage.tasks()).thenReturn(all);

        final Tasks tasks = new ProjectTasks(
            "john/test", Provider.Names.GITHUB,
            Stream::empty,
            storage
        );

        final Task result = tasks.assign(task, contract, days);
        MatcherAssert.assertThat(result, Matchers.is(assigned));
        Mockito.verify(all, Mockito.times(1)).assign(task, contract, days);
    }


    /**
     * Throws ISE when unasssigning Task is not part of ProjectTasks.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenUnassigningTaskNotPartOfProjectTasks(){
        final Task task = Mockito.mock(Task.class);
        final Task otherTask = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Project otherProject = Mockito.mock(Project.class);
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            () -> Stream.of(task),
            Mockito.mock(Storage.class)
        );

        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(otherProject.repoFullName()).thenReturn("john/test-other");
        Mockito.when(otherProject.provider()).thenReturn("github");
        Mockito.when(otherTask.project()).thenReturn(otherProject);

        tasks.unassign(otherTask);
    }

    /**
     * An assigned Task part of ProjectTasks can be unassigned.
     */
    @Test
    public void canBeUnassignedIfPartOfProjectTasks(){
        final Storage storage = Mockito.mock(Storage.class);
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = new ProjectTasks(
            "john/test", "github",
            () -> Stream.of(task),
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(storage.tasks()).thenReturn(Mockito.mock(Tasks.class));

        tasks.unassign(task);

        Mockito.verify(storage.tasks()).unassign(task);

    }

    /**
     * Mock an Issue for test.
     *
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
