package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

/**
 * Unit tests for {@link com.selfxdsd.core.tasks.ContractTasks}.
 * @author criske
 * @version $Id$
 * @since 0.0.6
 */
public final class ContractTasksTestCase {


    /**
     * ContractTasks should be iterable.
     */
    @Test
    public void canBeIterated() {
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
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
     * Returns the same tasks for the same contract id.
     */
    @Test
    public void returnsSameTasksOfSameContract(){
        final Contract.Id contractId = new Contract.Id("foo", "mihai",
            "github", "dev");
        final Tasks tasks = new ContractTasks(
            contractId,
            List.of(),
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks.ofContract(contractId),
            Matchers.is(tasks));
    }

    /**
     * Throws IllegalStateException when getting tasks for other contract.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenGettingTasksForOtherContract() {
        final Tasks contractTasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        );
        contractTasks.ofContract(new Contract.Id("other", "mihai2",
            "github", "dev"));
    }

    /**
     * Should throw when calling unassigned.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void throwsForUnassignedTasks(){
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).unassigned();
    }

    /**
     * Should register a new task associated with an issue.
     * Right now is throwing UOE.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void registerTask() {
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).register(Mockito.mock(Issue.class));
    }


    /**
     * Should return tasks of a project.
     */
    @Test
    public void returnsTasksOfProject(){
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(task),
            Mockito.mock(Storage.class)
        );

        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.repoFullName()).thenReturn("foo");
        Mockito.when(task.project()).thenReturn(project);

        MatcherAssert.assertThat(tasks.ofProject("foo", "github"),
            Matchers.iterableWithSize(1));
    }

    /**
     * Throws when already seeing the Project Tasks of Contract's Contributor.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenProjectTasksAlreadySeen() {
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).ofProject("other-foo", "github");
    }

    /**
     * Should return tasks of a contributor.
     */
    @Test
    public void returnsTasksOfContributor(){
        final Task task = Mockito.mock(Task.class);
        final Contributor assignee = Mockito.mock(Contributor.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(task),
            Mockito.mock(Storage.class)
        );

        Mockito.when(assignee.username()).thenReturn("mihai");
        Mockito.when(assignee.provider()).thenReturn("github");
        Mockito.when(task.assignee()).thenReturn(assignee);

        MatcherAssert.assertThat(tasks.ofContributor("mihai", "github"),
            Matchers.iterableWithSize(1));
    }

    /**
     * Throws when already seeing the Project Tasks of Contract's Contributor.
     */
    @Test(expected = IllegalStateException.class)
    public void throwsWhenContributorTasksAlreadySeen() {
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).ofContributor("john", "github");
    }


    /**
     * Should return a task by id.
     */
    @Test
    public void returnsTaskById(){
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Issue issue = Mockito.mock(Issue.class);
        final ContractTasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(task),
            Mockito.mock(Storage.class)
        );

        Mockito.when(issue.issueId()).thenReturn("123");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.repoFullName()).thenReturn("john/repo");
        Mockito.when(task.project()).thenReturn(project);

        MatcherAssert.assertThat(tasks
                .getById("123", "john/repo", "github"),
            Matchers.is(task));
    }

    /**
     * Should return null when task by id is not found.
     */
    @Test
    public void returnsNullWhenTaskByIdNotFound(){
        final Task task = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(),
            Mockito.mock(Storage.class)
        ).getById("123", "john/repo", "github");

        MatcherAssert.assertThat(task, Matchers.nullValue());
    }


}
