/**
 * Copyright (c) 2020-2021, Self XDSD Contributors
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
import com.selfxdsd.api.exceptions.TasksException;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Unit tests for {@link StoredTask}.
 *
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
            60,
            false,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(task.project(), Matchers.is(project));
    }

    /**
     * StoredTask can return its Unassigned Contract.
     */
    @Test
    public void returnsUnassignedContract() {
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            60,
            false,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.contract(),
            Matchers.allOf(
                Matchers.notNullValue(),
                Matchers.instanceOf(Contract.class)
            )
        );
    }

    /**
     * StoredTask can return its Contract.
     */
    @Test
    public void returnsContract() {
        final Contract contract = Mockito.mock(Contract.class);
        final Task task = new StoredTask(
            contract,
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            60,
            false
        );
        MatcherAssert.assertThat(
            task.contract(),
            Matchers.is(contract)
        );
    }

    /**
     * StoredTask can return its issueId.
     */
    @Test
    public void returnsIssueId() {
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "123",
            Contract.Roles.DEV,
            60,
            false,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(task.issueId(), Matchers.equalTo("123"));
    }

    /**
     * StoredTask can return its Issue from the Issues API, if
     * it's NOT a Pull Request.
     */
    @Test
    public void returnsIssueFromIssues() {
        final Issue issue = Mockito.mock(Issue.class);
        final Issues all = Mockito.mock(Issues.class);
        Mockito.when(all.getById("123")).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.issues()).thenReturn(all);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.repo("john", "test")).thenReturn(repo);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(provider);

        Mockito.when(project.projectManager()).thenReturn(manager);

        final Task task = new StoredTask(
            project,
            "123",
            Contract.Roles.DEV,
            60,
            false,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(task.issue(), Matchers.is(issue));
    }

    /**
     * StoredTask can return its Issue from the Pull Requests API, if
     * it IS a Pull Request.
     */
    @Test
    public void returnsIssueFromPullRequests() {
        final Issue issue = Mockito.mock(Issue.class);
        final Issues prs = Mockito.mock(Issues.class);
        Mockito.when(prs.getById("123")).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.pullRequests()).thenReturn(prs);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.repo("john", "test")).thenReturn(repo);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(provider);

        Mockito.when(project.projectManager()).thenReturn(manager);

        final Task task = new StoredTask(
            project,
            "123",
            Contract.Roles.DEV,
            60,
            true,
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
            60,
            false,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.role(),
            Matchers.equalTo(Contract.Roles.DEV)
        );
    }

    /**
     * Returns null when the Task is not assigned.
     */
    @Test
    public void returnsNullAssignee() {
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            60,
            false,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            task.assignee(), Matchers.nullValue()
        );
    }

    /**
     * StoredTask can return its assignment date.
     */
    @Test
    public void returnsAssignmentDate() {
        final LocalDateTime assignment = LocalDateTime.now();
        final Task task = new StoredTask(
            Mockito.mock(Contract.class),
            "issueId123",
            Mockito.mock(Storage.class),
            assignment,
            assignment.plusDays(10),
            0,
            false
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
            Mockito.mock(Contract.class),
            "issueId123",
            Mockito.mock(Storage.class),
            assignment,
            assignment.plusDays(10),
            0,
            false
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
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(mihai);

        final Task task = new StoredTask(
            contract,
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            0,
            false
        );
        MatcherAssert.assertThat(task.assignee(), Matchers.is(mihai));
    }

    /**
     * StoredTask can return its value based on the Contract's hourly rate and
     * its estimation.
     */
    @Test
    public void returnsValue() {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.hourlyRate()).thenReturn(
            BigDecimal.valueOf(50000)
        );

        final Task task = new StoredTask(
            contract,
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            30,
            false
        );

        MatcherAssert.assertThat(
            task.value(),
            Matchers.equalTo(BigDecimal.valueOf(25000))
        );
    }

    /**
     * StoredTask can return its value which is rounded half up.
     */
    @Test
    public void returnsValueRoundedUp() {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.hourlyRate()).thenReturn(
            BigDecimal.valueOf(25000)
        );

        final Task task = new StoredTask(
            contract,
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            45,
            false
        );

        MatcherAssert.assertThat(
            task.value(),
            Matchers.equalTo(BigDecimal.valueOf(18750))
        );
    }

    /**
     * StoredTask's value is zero if the Contract's hourly rate is zero.
     */
    @Test
    public void returnsZeroValue() {
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.hourlyRate()).thenReturn(
            BigDecimal.valueOf(0)
        );

        final Task task = new StoredTask(
            contract,
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            120,
            false
        );

        MatcherAssert.assertThat(
            task.value(),
            Matchers.equalTo(BigDecimal.valueOf(0))
        );
    }

    /**
     * StoredTask returns its estimation when the task is assigned (has a
     * contract).
     */
    @Test
    public void returnsEstimationAssigned() {
        final Task task = new StoredTask(
            Mockito.mock(Contract.class),
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            120,
            false
        );

        MatcherAssert.assertThat(
            task.estimation(),
            Matchers.equalTo(120)
        );
    }

    /**
     * When the StoredTask is not assigned to anyone, the estimation should be
     * 0.
     */
    @Test
    public void returnsEstimation() {
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            45,
            false,
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            task.estimation(),
            Matchers.equalTo(45)
        );
    }

    /**
     * StoredTask can be be marked as PR.
     */
    @Test
    public void returnsIsPullRequestFlag() {
        final Task task = new StoredTask(
            Mockito.mock(Project.class),
            "issueId123",
            Contract.Roles.DEV,
            45,
            true,
            Mockito.mock(Storage.class)
        );

        MatcherAssert.assertThat(
            task.isPullRequest(),
            Matchers.equalTo(Boolean.TRUE)
        );
    }

    /**
     * Can compare two StoredTask objects.
     */
    @Test
    public void comparesStoredTaskObjects() {
        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("123");
        final Issues all = Mockito.mock(Issues.class);
        Mockito.when(all.getById("123")).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.issues()).thenReturn(all);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");

        final Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.repo("john", "test")).thenReturn(repo);
        final ProjectManager manager = Mockito.mock(ProjectManager.class);
        Mockito.when(manager.provider()).thenReturn(provider);

        Mockito.when(project.projectManager()).thenReturn(manager);
        final Task task = new StoredTask(
            project,
            "123",
            "DEV",
            120,
            false,
            Mockito.mock(Storage.class)
        );
        final Task taskTwo = new StoredTask(
            project,
            "123",
            "DEV",
            120,
            false,
            Mockito.mock(Storage.class)
        );
        final Task taskThree = new StoredTask(
            project,
            "123",
            "DEV",
            120,
            true,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(task, Matchers.equalTo(taskTwo));
        MatcherAssert.assertThat(task, Matchers.not(
            Matchers.equalTo(taskThree)
        ));
    }

    /**
     * Verifies HashCode generation from StoredTask.
     */
    @Test
    public void verifiesStoredTaskHashcode() {
        final Contract contract = Mockito.mock(Contract.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/repo");
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        final Issue issue = Mockito.mock(Issue.class);
        final Issues all = Mockito.mock(Issues.class);
        Mockito.when(all.getById("123")).thenReturn(issue);
        final Repo repo = Mockito.mock(Repo.class);
        Mockito.when(repo.issues()).thenReturn(all);
        Mockito.when(project.repo()).thenReturn(repo);
        Mockito.when(contract.project()).thenReturn(project);
        final Task task = new StoredTask(
            contract,
            "123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            120,
            false
        );
        final Task taskTwo = new StoredTask(
            contract,
            "123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            120,
            false
        );
        final Task taskThree = new StoredTask(
            contract,
            "123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            120,
            true
        );
        MatcherAssert.assertThat(task.hashCode(),
            Matchers.equalTo(taskTwo.hashCode()));
        MatcherAssert.assertThat(task.hashCode(),
            Matchers.not(Matchers.equalTo(taskThree.hashCode())));
    }

    /**
     * Method StoredTask.assign(...) should throw an exception if
     * the task already has an assignee.
     */
    @Test (expected = TasksException.Single.Assign.class)
    public void assignComplainsIfTaskHasAssignee() {
        final Contributor assignee = Mockito.mock(Contributor.class);
        final Contract contract = Mockito.mock(Contract.class);
        Mockito.when(contract.contributor()).thenReturn(assignee);
        final Task task = new StoredTask(
            contract,
            "issueId123",
            Mockito.mock(Storage.class),
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(10),
            60,
            false
        );
        task.assign(Mockito.mock(Contributor.class));
    }

    /**
     * StoredTask.assign(...) complains if the given Contributor
     * does not have the required contract.
     */
    @Test (expected = TasksException.Single.Assign.class)
    public void assignComplainsIfContributorMissesContract() {
        final Contributor assignee = Mockito.mock(Contributor.class);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Task task = new StoredTask(
            project,
            "issueId123",
            "DEV",
            60,
            false,
            Mockito.mock(Storage.class)
        );
        Mockito.when(assignee.contract("john/test", "github", "DEV"))
            .thenReturn(null);
        task.assign(assignee);
    }

    /**
     * StoredTask.assign(...) can assign the given Contributor to the Task.
     */
    @Test
    public void assignsContributor() {
        final Storage storage = Mockito.mock(Storage.class);

        final Task assigned = Mockito.mock(Task.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        final Contract contract = Mockito.mock(Contract.class);

        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.repoFullName()).thenReturn("john/test");
        Mockito.when(project.provider()).thenReturn("github");
        final Task task = new StoredTask(
            project,
            "issueId123",
            "DEV",
            60,
            false,
            storage
        );
        Mockito.when(assignee.contract("john/test", "github", "DEV"))
            .thenReturn(contract);

        final Tasks all = Mockito.mock(Tasks.class);
        Mockito.when(all.assign(task, contract, 10)).thenReturn(assigned);
        Mockito.when(storage.tasks()).thenReturn(all);

        final Task result = task.assign(assignee);

        MatcherAssert.assertThat(
            result, Matchers.is(assigned)
        );
        Mockito.verify(all, Mockito.times(1)).assign(
            task, contract, 10
        );
    }

    /**
     * An assigned StoredTask can be unassigned.
     */
    @Test
    public void canBeUnassigned(){
        final Storage storage = Mockito.mock(Storage.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        final Contract contract = Mockito.mock(Contract.class);
        final Tasks all = Mockito.mock(Tasks.class);
        final Task task = new StoredTask(contract, "1", storage,
            LocalDateTime.now(), LocalDateTime.now().plusDays(2), 60,
            false);

        Mockito.when(contract.contributor()).thenReturn(assignee);
        Mockito.when(storage.tasks()).thenReturn(all);
        Mockito.when(all.unassign(Mockito.any(Task.class)))
            .thenReturn(Mockito.mock(Task.class));

        MatcherAssert.assertThat(task.assignee(),
            Matchers.notNullValue());
        MatcherAssert.assertThat(task.unassign().assignee(),
            Matchers.nullValue());

    }

    /**
     * An unassigned StoredTask returns itself when calling unassign.
     */
    @Test
    public void unassignedTaskReturnsItselfWhenUnassign(){
        final Task task = new StoredTask(Mockito.mock(Project.class),
            "1", "DEV", 60, false, Mockito.mock(Storage.class));

        MatcherAssert.assertThat(task.unassign(), Matchers.equalTo(task));
    }

    /**
     * Task can return its Resignations.
     */
    @Test
    public void returnsResignations(){
        final Storage storage = Mockito.mock(Storage.class);
        final Resignations all = Mockito.mock(Resignations.class);
        final Resignations ofTask = Mockito.mock(Resignations.class);
        final Task task = new StoredTask(Mockito.mock(Contract.class),
            "1", storage, LocalDateTime.now(),
            LocalDateTime.now().plusDays(2), 60, false);

        Mockito.when(storage.resignations()).thenReturn(all);
        Mockito.when(all.ofTask(task)).thenReturn(ofTask);

        MatcherAssert.assertThat(task.resignations(), Matchers.equalTo(ofTask));
    }
}
