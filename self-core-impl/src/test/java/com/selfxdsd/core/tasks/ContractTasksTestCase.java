package com.selfxdsd.core.tasks;

import com.selfxdsd.api.*;
import com.selfxdsd.api.exceptions.TasksException;
import com.selfxdsd.api.storage.Storage;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.stream.Stream;

/**
 * Unit tests for {@link com.selfxdsd.core.tasks.ContractTasks}.
 * @author criske
 * @version $Id$
 * @since 0.0.6
 * @todo #1237:30min Write remaining testcases for updating estimation
 *  in {@link ContributorTasks} and {@link ProjectTasks}.
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
            )::stream,
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
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        MatcherAssert.assertThat(tasks.ofContract(contractId),
            Matchers.is(tasks));
    }

    /**
     * Throws Self Exception when getting tasks for other contract.
     */
    @Test(expected = TasksException.OfContract.List.class)
    public void throwsWhenGettingTasksForOtherContract() {
        final Tasks contractTasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
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
            Stream::empty,
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
            Stream::empty,
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
            List.of(task)::stream,
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
    @Test(expected = TasksException.OfProject.List.class)
    public void throwsWhenProjectTasksAlreadySeen() {
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
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
            List.of(task)::stream,
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
    @Test(expected = TasksException.OfContributor.List.class)
    public void throwsWhenContributorTasksAlreadySeen() {
        new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
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
        final ContractTasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            List.of(task)::stream,
            Mockito.mock(Storage.class)
        );

        Mockito.when(task.issueId()).thenReturn("123");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(project.repoFullName()).thenReturn("john/repo");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(task.isPullRequest()).thenReturn(Boolean.FALSE);

        MatcherAssert.assertThat(
            tasks.getById(
                "123",
                "john/repo",
                "github",
                Boolean.FALSE
            ),
            Matchers.is(task)
        );
    }

    /**
     * Should return null when task by id is not found.
     */
    @Test
    public void returnsNullWhenTaskByIdNotFound(){
        final Task task = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
            Mockito.mock(Storage.class)
        ).getById(
            "123",
            "john/repo",
            "github",
            Boolean.TRUE
        );

        MatcherAssert.assertThat(task, Matchers.nullValue());
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

        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
            storage
        );
        final Task result = tasks.assign(task, contract, days);
        MatcherAssert.assertThat(result, Matchers.is(assigned));
        Mockito.verify(all, Mockito.times(1)).assign(task, contract, days);
    }

    /**
     * Throws Self Exception when unasssigning Task is not part
     * of ContractTasks.
     */
    @Test(expected = TasksException.OfContract.NotFound.class)
    public void throwsWhenUnassigningTaskNotPartOfContract(){
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        Mockito.when(task.issueId()).thenReturn("123");
        Mockito.when(project.provider()).thenReturn("gitlab");
        Mockito.when(project.repoFullName()).thenReturn("john/repo");
        Mockito.when(task.project()).thenReturn(project);

        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
            Mockito.mock(Storage.class)
        );
        tasks.unassign(task);
    }

    /**
     * An assigned Task part of ContractTasks can be unassigned.
     */
    @Test
    public void canBeUnassignedIfPartOfContract(){
        final Task task = Mockito.mock(Task.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            () -> Stream.of(task),
            storage
        );

        Mockito.when(storage.tasks()).thenReturn(Mockito.mock(Tasks.class));

        tasks.unassign(task);
        Mockito.verify(storage.tasks()).unassign(task);
    }

    /**
     * Can remove a task from storage if task is part of ContractTasks.
     */
    @Test
    public void removesTask() {
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            () -> Stream.of(task),
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("foo");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(task.issueId()).thenReturn("1");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(storage.tasks()).thenReturn(all);

        tasks.remove(task);
        Mockito.verify(storage.tasks(), Mockito.times(1)).remove(task);
    }

    /**
     * Throws Self Exception when task is not part of ContractTasks.
     */
    @Test(expected = TasksException.OfContract.NotFound.class)
    public void throwsWhenRemovingTaskNotPartOf() {
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("foo");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(task.issueId()).thenReturn("1");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(storage.tasks()).thenReturn(all);

        tasks.remove(task);
    }

    /**
     * An assigned Task part of ContractTasks can update its estimation.
     */
    @Test
    public void canUpdateEstimationIfPartOfContract(){
        final Task task = Mockito.mock(Task.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            () -> Stream.of(task),
            storage
        );

        Mockito.when(storage.tasks()).thenReturn(Mockito.mock(Tasks.class));

        tasks.updateEstimation(task, 30);
        Mockito.verify(storage.tasks()).updateEstimation(task, 30);
    }

    /**
     * An assigned Task not part of ContractTasks fails updating its
     * estimation.
     */
    @Test(expected = TasksException.OfContract.NotFound.class)
    public void canFailUpdateEstimationIfNotPartOfContract(){
        final Task task = Mockito.mock(Task.class);
        final Project project = Mockito.mock(Project.class);
        final Issue issue = Mockito.mock(Issue.class);
        final Storage storage = Mockito.mock(Storage.class);
        final Tasks all = Mockito.mock(Tasks.class);
        final Tasks tasks = new ContractTasks(
            new Contract.Id("foo", "mihai",
                "github", "dev"),
            Stream::empty,
            storage
        );

        Mockito.when(project.repoFullName()).thenReturn("foo");
        Mockito.when(project.provider()).thenReturn("github");
        Mockito.when(task.project()).thenReturn(project);
        Mockito.when(issue.issueId()).thenReturn("1");
        Mockito.when(task.issueId()).thenReturn("1");
        Mockito.when(task.issue()).thenReturn(issue);
        Mockito.when(storage.tasks()).thenReturn(all);

        tasks.updateEstimation(task, 30);
    }

}
