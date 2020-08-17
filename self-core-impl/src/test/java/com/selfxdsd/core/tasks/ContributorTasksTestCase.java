package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

/**
 * Unit tests for {@link ContributorTasks}.
 *
 * @author criske
 * @version $Id$
 * @since 0.0.1
 */
public final class ContributorTasksTestCase {

    /**
     * ContributorTasks should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Tasks tasks = new ContributorTasks(
            "foo",
            "github",
            List.of(Mockito.mock(Task.class),
                Mockito.mock(Task.class),
                Mockito.mock(Task.class))::stream,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks, Matchers.iterableWithSize(3));
    }

    /**
     * Should register a new task associated with an issue. Right now is
     * throwing UOE.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void registerTask() {
        final Issue issue = this.mockIssue(
            "123",
            "john/other",
            "github",
            Contract.Roles.DEV
        );
        final Tasks tasks = new ContributorTasks(
            "foo", "github", Stream::empty,
            Mockito.mock(Storage.class)
        );
        tasks.register(issue);
    }

    /**
     * ContributorTask.ofContributor returns self if the ID matches.
     */
    @Test
    public void ofContributorReturnsSelf() {
        final Tasks tasks = new ContributorTasks(
            "foo", "gitlab", Stream::empty,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(
            tasks.ofContributor("foo", "gitlab"),
            Matchers.is(tasks)
        );
    }

    /**
     * ContributorTasks.ofContributor should complain if the ID of a different
     * contributor is specified.
     */
    @Test(expected = IllegalStateException.class)
    public void ofContributorComplainsOnDifferentId() {
        final Tasks tasks = new ContributorTasks(
            "foo", "gitlab", Stream::empty,
            Mockito.mock(Storage.class)
        );
        tasks.ofContributor("bar", "gitlab");
    }

    /**
     * Should return tasks of a project.
     */
    @Test
    public void tasksOfProject() {
        final Storage storage = Mockito.mock(Storage.class);

        final Project projectOne = Mockito.mock(Project.class);
        Mockito.when(projectOne.repoFullName()).thenReturn("mihai");
        Mockito.when(projectOne.provider()).thenReturn(Provider.Names.GITHUB);
        final Task taskOne = Mockito.mock(Task.class);
        Mockito.when(taskOne.project()).thenReturn(projectOne);

        final Project projectTwo = Mockito.mock(Project.class);
        Mockito.when(projectTwo.repoFullName()).thenReturn("mihai/other");
        Mockito.when(projectTwo.provider()).thenReturn(Provider.Names.GITLAB);
        final Task taskTwo = Mockito.mock(Task.class);
        Mockito.when(taskTwo.project()).thenReturn(projectTwo);

        final Tasks tasks = new ContributorTasks(
            "mihai", Provider.Names.GITHUB,
            List.of(taskOne, taskTwo)::stream,
            storage
        );

        MatcherAssert.assertThat(
            tasks.ofProject("mihai", Provider.Names.GITHUB),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * Should return tasks for contract.
     */
    @Test
    public void returnTasksForContract() {
        final Contract.Id contractId = new Contract.Id("foo", "mihai",
            Provider.Names.GITHUB, Contract.Roles.DEV);

        final Contributor mihai = Mockito.mock(Contributor.class);
        Mockito.when(mihai.username()).thenReturn("mihai");
        Mockito.when(mihai.provider()).thenReturn(Provider.Names.GITHUB);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.repoFullName()).thenReturn("foo");
        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.assignee()).thenReturn(mihai);
        Mockito.when(task.role()).thenReturn(Contract.Roles.DEV);
        Mockito.when(task.project()).thenReturn(project);

        final Storage storage = Mockito.mock(Storage.class);

        final Tasks tasks = new ContributorTasks(
            "foo", Provider.Names.GITHUB,
            List.of(task)::stream,
            storage
        );
        MatcherAssert.assertThat(tasks.ofContract(contractId),
            Matchers.iterableWithSize(1)
        );
    }

    /**
     * Throws UnsupportedOperationException. Contributor should not have
     * unassigned tasks.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsWhenReturnsUnassignedTasks() {
        new ContributorTasks(
            "mihai", Provider.Names.GITHUB,
            Stream::empty, Mockito.mock(Storage.class))
            .unassigned();
    }


    /**
     * Returns a Task by its composite id.
     */
    @Test
    public void returnsTaskById(){
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.repoFullName()).thenReturn("mihai/repo");

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issue()).thenReturn(issue);

        Mockito.when(task.project()).thenReturn(project);

        final Storage storage = Mockito.mock(Storage.class);

        final Task found = new ContributorTasks(
            "mihai", Provider.Names.GITHUB,
            List.of(task)::stream,
            storage
        ).getById("1", "mihai/repo", Provider.Names.GITHUB);
        MatcherAssert.assertThat(found, Matchers.is(task));
    }

    /**
     * Returns null when Task by its composite id was not found.
     */
    @Test
    public void returnsNullWhenTaskByIdNotFound(){
        final Project project = Mockito.mock(Project.class);
        Mockito.when(project.provider()).thenReturn(Provider.Names.GITHUB);
        Mockito.when(project.repoFullName()).thenReturn("mihai/repo");

        final Issue issue = Mockito.mock(Issue.class);
        Mockito.when(issue.issueId()).thenReturn("1");

        final Task task = Mockito.mock(Task.class);
        Mockito.when(task.issue()).thenReturn(issue);

        Mockito.when(task.project()).thenReturn(project);

        final Storage storage = Mockito.mock(Storage.class);

        final Task found = new ContributorTasks(
            "mihai", Provider.Names.GITHUB,
            List.of(task)::stream,
            storage
        ).getById("2", "mihai/repo", Provider.Names.GITHUB);
        MatcherAssert.assertThat(found, Matchers.nullValue());
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

        final Tasks tasks = new ContributorTasks(
            "mihai", Provider.Names.GITHUB,
            List.of(task)::stream,
            storage
        );
        final Task result = tasks.assign(task, contract, days);
        MatcherAssert.assertThat(result, Matchers.is(assigned));
        Mockito.verify(all, Mockito.times(1)).assign(task, contract, days);
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
